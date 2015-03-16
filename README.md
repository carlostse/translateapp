## Server ##

The server side is written in node.js

### start ###

To install the dependencies, run `npm install`

$ npm install

To start the server, run `npm start`

$ npm start

Ensure you got node in your $PATH, for Ubuntu, it uses nodejs instead of node,
please create a symbolic link before start the server

$ sudo ln -s /usr/bin/nodejs /usr/bin/node

### dictionary ###

A 3rd part dictionary, CC-CEDICT, is used for the translate.

http://www.mdbg.net/chindict/chindict.php?page=cedict
http://en.wikipedia.org/wiki/CEDICT

The server API load the dictionary into a dictionary / map in memory at start,
and query the user request in the dictionary, then return the result in JSON format.

### API ###
There are two APIs, one hosted in AWS and the other hosted in my server

[Host] http://aws.aboutme.com.hk:3000

### translate ###

/translate?word=PARM1

PARM1: the word need to be translated, UTF-8 and URL encoded.
Case insensitive, multiple words are support.

A JSON will be return for the tranlate result.

If no error, the message will be "OK", and the result can be found in "output".

{
  "output": <string: tranlate result>,
  "message": <string: system message>
}

#### examples ####

Translate one word, for example:

http://aws.aboutme.com.hk:3000/translate?word=one

{"output":"一","message":"OK"}

Multiple words are support, for example:

http://aws.aboutme.com.hk:3000/translate?word=one%20two%20three

{"output":"一 二 三","message":"OK"}

Case insensitive and multiple words

http://aws.aboutme.com.hk:3000/translate?word=One%20Two

{"output":"一 二","message":"OK"}

More than one space is also handled, for example:

http://aws.aboutme.com.hk:3000/translate?word=One%20%20Two%20%20%20%20THREE

{"output":"一 二 三","message":"OK"}

#### error message ####

If one or more words that cannot be translated, an error message will be return,
and the word which cannot be translated will be stated, for example:

http://aws.aboutme.com.hk:3000/translate?word=ibm
or
http://aws.aboutme.com.hk:3000/translate?word=one%20ibm

{"output":"","message":"Cannot translate: ibm"}

### dictionary ###

/dictionary

It will return the dictionary in plain text format, for example:

http://aws.aboutme.com.hk:3000/dictionary

## App ##

The app use SQLite for the cache (bonus part) and tranlate history.

The app will query the cache table to check if the word has been translated before,
if so, it will load the result from cache, otherwise, it will query the API.

And, the tranlate history will be saved to history table, just like those chat IM app.

### database ###

Two tables are used:

[cache]
source TEXT (PRIMARY KEY)
result TEXT

[history]
ROWID (hidden, SQLite build-in) (PRIMARY KEY)
message TEXT
type INTEGER ([enum] Send: 0, Receive: 1)

The database operation is done by com.iems5722.translateapp.util.Database,
and wrapped the save / get function in the History object for translate history.

### translate ###

This app can support different translate APIs:

enum TranlateMethod {LocalDict, TCP, HTTP, SelfAPI}

LocalDict: the local dictionary used in assigment 1
TCP: the TCP socket API used in assigment 2
HTTP: the HTTP API used in assigment 2
SelfAPI: the self-implemented server API used in assignment 4

And, by default, the self-implemented server API is used:

private final TranlateMethod method = TranlateMethod.SelfAPI;

### sharing ###

The app can receive plain/text message from other apps,
and can send the latest tranlate result to other apps.