    app.controller('HomeController', ['MainService', function (MainService) {
    var self = this;

    self.deviceTypes = [];

    self.init = function() {
        MainService.getDeviceTypes(self);
    }

}])