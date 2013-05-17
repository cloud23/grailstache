class UrlMappings {

	static mappings = {
		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		//"/"(redirect:"/song/list")
		"/" {
	    controller = "song"
      destination = "/list"
    }
		"500"(view:'/error')
	}
}
