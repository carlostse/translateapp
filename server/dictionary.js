/**
 * Module dependencies
 */
var fs = require('fs');

// constructor
var Dictionary = function(file){
    this.file = file;
    this.loadOptions = {encoding: 'utf-8', flag: 'r'};

    // load dictionary
    console.log('load dictionary from ' + this.file);
    var data = fs.readFileSync(this.file, this.loadOptions)
      , dict = {};

    // add to pairs map
    data.split('\n').forEach(function(line){
        if (line && line[0] != '#' && line[0] != '%'){
            var chi = line.split(' ')[0]
              , eng = line.split('/'); // from [1] to end

            // add to dictionary
            eng.forEach(function(word, idx){
                if (idx > 0 && /* ignore the first elements */
                    word && word.trim().length > 0 && /* ignore empty words */
                    word.indexOf('CL:') != 0 && /* ignore CL: */
                    word.indexOf(' ') == -1 /* ignore phrase */){
                    // only add new words
                    if (!dict[word]) dict[word] = chi;
                }
            });
        }

    });
    console.log('load dictionary done');
    this.dict = dict;
};

// class methods
Dictionary.prototype.translate = function(word, delegate){
    var result = ''
      , err = ''
      , errArray = [];

    word.split(' ').forEach(function(w, i){
        // case insensitive and ignore more than one space
        var t = w && w.length > 0? w.trim().toLowerCase(): null;
        if (t && t.length > 0){
            // separate words by space
            if (result.length > 0) result += ' ';
            
            var r = this[t];
            // append the result
            // for those words which cannot be translated,
            // add to error array
            if (r)
                result += r;
            else
                errArray.push(w);
        }
    }, this.dict);

    // if error array is not empty, add error text
    if (errArray.length > 0)
        err = 'Cannot translate: ' + errArray.join(', ');

    console.log(word + ': ' + result + ', err: ' + err);
    delegate(result, err);
};

// return Dictionary for require call
module.exports = Dictionary;