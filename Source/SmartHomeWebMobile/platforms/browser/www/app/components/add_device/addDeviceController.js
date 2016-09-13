app.controller('AddDeviceController', function($location) {
    var self = this;

    self.name = "";
    self.type = "";
    self.gpio = null;
    self.description = "";

    self.addDevice = function() {
        $location.path("/home");
    }
})