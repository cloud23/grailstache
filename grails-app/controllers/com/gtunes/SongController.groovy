package com.gtunes

import java.io.Reader
import java.io.BufferedReader
import com.sampullara.util.FutureWriter
import com.sampullara.mustache.MustacheBuilder
import org.springframework.dao.DataIntegrityViolationException

class SongController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        //redirect(action: "list", params: params)
      def template = applicationContext.getResourceByPath("/templates/song/index.mustache")?.getFile() //"Hello {{fullname}}. {{#isLoggedIn}} We're glad you logged in. {{/isLoggedIn}}"
  		String op = compileMustache([fullname: 'efren', isLoggedIn: true], new BufferedReader(new FileReader(template)))
  		render op
    }

    def list(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        [songInstanceList: Song.list(params), songInstanceTotal: Song.count()]
    }

    def create() {
        [songInstance: new Song(params)]
    }

    def save() {
        def songInstance = new Song(params)
        if (!songInstance.save(flush: true)) {
            render(view: "create", model: [songInstance: songInstance])
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

        [songInstance: songInstance]
    }

    def edit(Long id) {
        def songInstance = Song.get(id)
        if (!songInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'song.label', default: 'Song'), id])
            redirect(action: "list")
            return
        }

        [songInstance: songInstance]
    }

    def update(Long id, Long version) {
        def songInstance = Song.get(id)
        if (!songInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'song.label', default: 'Song'), id])
            redirect(action: "list")
            return
        }

        if (version != null) {
            if (songInstance.version > version) {
                songInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'song.label', default: 'Song')] as Object[],
                          "Another user has updated this Song while you were editing")
                render(view: "edit", model: [songInstance: songInstance])
                return
            }
        }

        songInstance.properties = params

        if (!songInstance.save(flush: true)) {
            render(view: "edit", model: [songInstance: songInstance])
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
    
    new MustacheBuilder()
            .build(reader, "mustacheOutput")
            .execute(writer, model as Map)
    writer.flush()

    return baos.toString()    
  }
}
