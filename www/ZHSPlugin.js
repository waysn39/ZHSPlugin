var exec = require('cordova/exec');
function ZHSPlugin(){};

ZHSPlugin.prototype.printPdf = function (arg0, success, error) {
    exec(success, error, 'ZHSPlugin', 'printPdf', [arg0]);
};
ZHSPlugin.prototype.execSQL = function(arg0,success,error){
    exec(success,error,'ZHSPlugin', 'execSQL', [arg0]);
};
ZHSPlugin.prototype.getDataSet = function(arg0,arg1,arg2,arg3,arg4,arg5,success,error){
    exec(success,error,'ZHSPlugin', 'getDataSet',  [arg0,arg1,arg2,arg3,arg4,arg5]);
};
ZHSPlugin.prototype.updateTable = function(arg0,arg1,arg2,arg3,arg4,success,error){
    exec(success,error,'ZHSPlugin', 'updateTable', [arg0,arg1,arg2,arg3,arg4]);
};
ZHSPlugin.prototype.deleteTable = function(arg0,arg1,arg2,success,error){
    exec(success,error,'ZHSPlugin', 'deleteTable', [arg0,arg1,arg2]);
};
ZHSPlugin.prototype.insertTable = function(arg0,arg1,arg2,success,error){
    exec(success,error,'ZHSPlugin', 'insertTable', [arg0,arg1,arg2]);
};

var zhsPlugin = new ZHSPlugin();
module.exports = zhsPlugin;


