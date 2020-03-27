# Code Orange (Covid 19)
**The main goal**:  Help people to get know if they were in touch with an infected person using public APIs and users location (including history)

**THIS IS AN OPEN-SOURCE PROJECT. **
**Please take it, change it, use it as you like.**
We build this project to help people get to know if there were in touch with infected people.

We encourage anyone in the world not to wait for government to build the app for them but use what we build and adopt to their country and API of infected persons.

### Main Functionality:
The application collects location in background and matches it with data from the Israeli Ministry of health. (you can easily switch it to different API)
Also, it uses chrome app to download file location history from Google TimeLine, later it parses those downloaded files from the "Download folder" and add it to the map and match algorithm
Data stored locally in SQLite and nothing is sent to any server. (We wanted to build API with Ministry of Health but stop due to lack of collaboration)

![Demo](demo/demo.gif)


### High Level Architecture:
[![Architecture](demo/architecture.png)](demo/architecture.png)


**Contributors**

The app is built with help of developers from [Android Academy in Tel Aviv](https://www.facebook.com/groups/android.academy.ils/) and [Nexar](https://www.getnexar.com) especially Big thanks to Artyom Okun, Yonatan Levin, Eden Bugdary & Daniel Szasz for their help at nights and weekend with this project

Also many thanks to those who contributed to the translation of the app
|Language| Translator |  Organization |
|--|--|--|
| English | Developers | Android Academy |
| Hebrew | Developers | Android Academy |
| Arabic | [Nasim Khoury](mailto:nasim@glocaltrans.com) | [GLOCAL - Translation & Content Solutions](www.glocaltrans.com) |
| Arabic | [Yosif Masarweh](mailto:yosiftbt@gmail.com) |  |
