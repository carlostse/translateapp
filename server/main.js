/**
 * Module dependencies and constants
 */
var express = require('express')
  , fs = require('fs')
  , app = express()
  , Dictionary = require('./dictionary')
  , SERVER_PORT = 3000
  , SUCCESS = 'OK';

/**
 * Dictionary
 */
var dict = new Dictionary('./cedict_1_0_ts_utf-8_mdbg.txt');

/**
 * Pages
 */
app.get('/translate', function(req, res) {
    var word = req.query.word;
    console.log('translate [' + word + ']');

    // validate
    if (!word){
        res.set('Content-Type', 'text/plain');
        res.send(JSON.stringify({
            output: '',
            message: 'Missing input'
        }));
        return;
    }

    // get from dictionary
    dict.translate(word, function(result, err){
        var resp = {};

        // if err is empty, result must not be empty
        if (err && err.length > 0){
            resp.output = '';
            resp.message = err;
        } else {
            resp.output = result;
            resp.message = 'OK';
        }

        res.set('Content-Type', 'application/json');
        res.status(200).send(JSON.stringify(resp));
    });
});

app.get('/dictionary', function (req, res){
    fs.readFile(dict.file, dict.loadOptions, function(err, data) {
        res.set('Content-Type', 'text/plain');
        if (err)
            res.status(500).send('cannot load dictionary');
        else
            res.status(200).send(data);
    });
});

app.get('/', function (req, res){
    res.status(404).send('404 - NOT FOUND');
});

/**
 * Server
 */
var server = app.listen(SERVER_PORT, function (){
    var host = server.address().address
      , port = server.address().port;
    console.log('server listening at http://%s:%s', host, port)
})
