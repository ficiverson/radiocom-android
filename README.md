# radiocom-android
This is an Android application for Community Media Stations.
## Getting Started
This app is supposed to be configurable.

This app has a newsreader, live broadcast , podcast, photo gallery and integrations with twitter and facebook.

### Prerequisities to work
We need two server call to work properly. 

1. Station call which returns a StationTO. Example of [stations] (https://radiocom.stamplayapp.com/api/cobject/v1/radiostation)
  ```JSON
  {"data":[{"_id":"57c8062520c6079304a2ddc4","big_icon_url":"https://pbs.twimg.com/profile_images/2532032956/i2da9drz65bguq6vdj43.jpeg","history":"<h3>Benvid@ a radio comunitaria da Coruña. CUAC FM emitindo no 103.4 FM desde 1996.</h3><img src=\"https://upload.wikimedia.org/wikipedia/commons/thumb/1/16/Estudio_Jose_Couso_-_Cuac_FM.001.JPG/220px-Estudio_Jose_Couso_-_Cuac_FM.001.JPG\" alt=\"Smiley face\"><p>Se tes interés en facer un programa de radio contacta a través do correo electrónico e te mandaremos a información.","icon_url":"https://pbs.twimg.com/profile_images/2532032956/i2da9drz65bguq6vdj43.jpeg","latitude":"-9,2","longitude":"40,32","news_rss":"https://cuacfm.org/feed/","station_name":"CUAC FM","station_photos":["https://cuacfm.org/wp-content/uploads/2015/04/cousomicros1.jpg","http://fotos00.laopinioncoruna.es/2015/12/10/646x260/cuac-fm.jpg"],"stream_url":"https://streaming.cuacfm.org/cuacfm-128k.mp3"} 
  ```

2. Podcast call which returns s ProgramTO. Example of [programs] (https://radiocom.stamplayapp.com/api/cobject/v1/program)
  ```JSON
   "data":[{"_id":"57c801c620c6079304a2dda9","description":"El programa de series en serio","logo_url":"http://image","rss_url":"http://podcastrss","title":"Spoiler"},{"_id":"57c8039b20c6079304a2ddc2","description":"Irmans","logo_url":"http://image","rss_url":"http://podcastrss","title":"Alegría","appId":"radiocom"}].
   ```

### Configuration items

#### Main files to modify:
1. GlobalValues.java--> you can set some properties like baseURL to connect with server side
2. strings.xml--> you can set all texts in different languages
3. colors.xml--> you can set the main color scheme of the app
  


### Some screenshots

![Alt text](https://dl.dropboxusercontent.com/u/30278258/radiocom/compo1.png?raw=true "News, station, etc.")

![Alt text](https://dl.dropboxusercontent.com/u/30278258/radiocom/compo2.png?raw=true "Streaming, podcasting")


## Contributing

Any help is welcome

## License

  Copyright 2016 Fernando Souto González

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## Author
 **Fernando Souto** - *Initial work* - [ficiverson](https://github.com/ficiverson) [@ficiverson](https://twitter.com/ficiverson)

## Gratefulness

Thanks to the creators of the following libraries:

    compile 'org.springframework.android:spring-android-rest-template:2.0.0.M3'
    compile group: 'com.google.code.gson', name: 'gson', version: '2.7'
    compile 'com.wang.avi:library:2.1.3'
    compile 'com.squareup.picasso:picasso:2.5.2'
    compile 'com.pkmmte.pkrss:pkrss:1.2'
    compile 'com.squareup.okhttp:okhttp:2.0.0'
    compile 'com.squareup.okhttp:okhttp-urlconnection:2.0.0'


