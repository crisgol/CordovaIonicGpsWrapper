module.exports = {
    GetCurrentLocation: function (successCallback, errorCallback) {
        console.log('CordovaIonicGpsWrapper called');
        cordova.exec(successCallback, errorCallback, "CordovaIonicGpsWrapper", "GetCurrentLocation");
    }
};