package com.gtunes

import java.io.Reader
import java.io.BufferedReader
import com.sampullara.util.FutureWriter
import com.sampullara.mustache.MustacheBuilder
import org.springframework.dao.DataIntegrityViolationException
import java.util.regex.*

class SongController {

  static allowedMethods = [save: "POST", update: "POST"]

  def index() {
      //redirect(action: "list", params: params)
    redirect(action: "list")
  }

  def list(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    //[songInstanceList: Song.list(params), songInstanceTotal: Song.count()]
    def sl = [pageTitle: "Song Lists", songs: Song.list(params)]
    def template = applicationContext.getResourceByPath("/templates/song/list.mustache")?.getFile() 
		String op = compileMustache(sl, new BufferedReader(new FileReader(template)))
		render op   
  }

  def create() {
  	def mod = [action: "Create", song: null]
    def template = applicationContext.getResourceByPath("/templates/song/create.mustache")?.getFile() 
		String op = compileMustache(mod, new BufferedReader(new FileReader(template)))
		render op
  }

  def save() {
    def songInstance = new Song(params)
    if (!songInstance.save(flush: true)) {
        redirect(action: "create", model: [songInstance: songInstance])
        return
    }

    flash.message = message(code: 'default.created.message', args: [message(code: 'song.label', default: 'Song'), songInstance.id])
    redirect(action: "show", id: songInstance.id)
  }

  def show(Long id) {
    def songInstance = Song.get(id)
    if (!songInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'song.label', default: 'Song'), id])
        redirect(action: "list")
        return
    }
    def si = [song: songInstance]
    def template = applicationContext.getResourceByPath("/templates/song/show.mustache")?.getFile() 
		String op = compileMustache(si, new BufferedReader(new FileReader(template)))
		render op
  }

  def edit(Long id) {
    def songInstance = Song.get(id)
    def mod = [action: "Save", song: songInstance]
    if (!songInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'song.label', default: 'Song'), id])
        redirect(action: "list")
        return
    }

    def template = applicationContext.getResourceByPath("/templates/song/edit.mustache")?.getFile() 
		String op = compileMustache(mod, new BufferedReader(new FileReader(template)))
		render op
  }

  def update() {
    def songInstance = Song.get(params.id)
    println(params.id)
    if (!songInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'song.label', default: 'Song'), params.id])
        redirect(action: "list")
        return
    }

    if (songInstance.version != null) {
        if (songInstance.version > params.version) {
            songInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                      [message(code: 'song.label', default: 'Song')] as Object[],
                      "Another user has updated this Song while you were editing")
            redirect(action: "edit", id: songInstance.id)
            return
        }
    }

    songInstance.properties = params

    if (!songInstance.save(flush: true)) {
        redirect(action: "edit", id: songInstance.id)
        return
    }

    flash.message = message(code: 'default.updated.message', args: [message(code: 'song.label', default: 'Song'), songInstance.id])
    redirect(action: "show", id: songInstance.id)
  }

  def delete(Long id) {
    def songInstance = Song.get(id)
    if (!songInstance) {
        flash.message = message(code: 'default.not.found.message', args: [message(code: 'song.label', default: 'Song'), id])
        redirect(action: "list")
        return
    }

    try {
        songInstance.delete(flush: true)
        flash.message = message(code: 'default.deleted.message', args: [message(code: 'song.label', default: 'Song'), id])
        redirect(action: "list")
    }
    catch (DataIntegrityViolationException e) {
        flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'song.label', default: 'Song'), id])
        redirect(action: "show", id: id)
    }
  }

  private def compileMustache(def model, Reader reader) {
    java.io.ByteArrayOutputStream baos = new ByteArrayOutputStream()
    FutureWriter writer = new FutureWriter(new OutputStreamWriter(baos))
    
   	//String rdrStr = reader.getText()
   	//rdrStr.find(~/\[\[>/) { match, found ->
    //     println(found)
    //     return found
    //}\[\[
    //rdrStr.replaceAll("template", "rocks!")
   	//def rdr = new StringReader(rdrStr)
    
    new MustacheBuilder()
            .build(reader, "mustacheOutput")
            .execute(writer, model as Map)
    writer.flush()

    return baos.toString()    
  }
}
