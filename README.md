# PopMovies
If you want to run this app please add below in your gradle.properties files
#My secret Key
API_KEY="your secret api code"

You can get a API key by registering with https://api.themoviedb.org

The next step will then be to add this in your build.gradle(Module:app)
buildConfigField("String", "API_KEY", API_KEY)

add it in the defaultConfig node.
