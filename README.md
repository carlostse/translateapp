== Server ==

The server side is written in node.js

A 3rd part dictionary, CC-CEDICT, is used for the translate.

http://www.mdbg.net/chindict/chindict.php?page=cedict
http://en.wikipedia.org/wiki/CEDICT

The server API load the dictionary into a dictionary / map in memory at start,
and query the user request in the dictionary, then return the result in JSON format.

=== API ===
There are two APIs, one hosted in AWS and the other hosted in my server

http://aws.aboutme.com.hk:3000/translate?word=

http://api.aboutme.com.hk:8086/translate?word=

=== API result ===

A JSON will be return for the tranlate result.

If no error, the message will be "OK", for example:

http://aws.aboutme.com.hk:3000/translate?word=one%20two%20three

{"output":"一","message":"OK"}

Multiple words are support, for example:

http://api.aboutme.com.hk:8086/translate?word=one%20two%20three

{"output":"一 二 三","message":"OK"}

=== API error message ===

If one or more words that cannot be translated, an error message will be return,
and the word which cannot be translated will be stated, for example:

http://aws.aboutme.com.hk:3000/translate?word=ibm
or
http://aws.aboutme.com.hk:3000/translate?word=one%20ibm

{"output":"","message":"Cannot translate: ibm"}

== App ==

The app use SQLite for the cache (bonus part) and tranlate history.

The app will query the cache table to check if the word has been translated before,
if so, it will load the result from cache, otherwise, it will query the API.

And, the tranlate history will be saved to history table, just like those chat IM app.

=== database ===

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

=== sharing ===

The app can receive plain/text message from other apps,
and can send the latest tranlate result to other apps.