package com.gtunes

import java.io.Reader
import java.io.BufferedReader
import com.sampullara.util.FutureWriter
import com.sampullara.mustache.MustacheBuilder

@Mixin(MustacheCompiler)
class SongController {
	def mustacheCompiler

  def index() { 
  	def template = applicationContext.getResourceByPath("/templates/song/index.mustache")?.getFile() //"Hello {{fullname}}. {{#isLoggedIn}} We're glad you logged in. {{/isLoggedIn}}"
  	String op = compileMustache([fullname: 'efren', isLoggedIn: true], new BufferedReader(new FileReader(template)))
  	render op
  }
}
