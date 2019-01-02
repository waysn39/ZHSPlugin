var exec = require('cordova/exec');

exports.printPdf = function (arg0, success, error) {
    exec(success, error, 'ZHSPlugin', 'printPdf', [arg0]);
};
