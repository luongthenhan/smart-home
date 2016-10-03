<<<<<<< HEAD
app.controller('HomeController', ['MainService', function (MainService) {
    var self = this;

    self.deviceTypes = [];
    self.modes = [];
    self.selectedMode = null;

    self.init = function() {
        MainService.getDeviceTypes(self);
        MainService.getModes(self);
    }

=======
    app.controller('HomeController', ['MainService', function (MainService) {
    var self = this;

    self.deviceTypes = [];

    self.init = function() {
        MainService.getDeviceTypes(self);
    }

>>>>>>> 0704e7edb4a1e704f859268bfa4c87efd7acf1d6
}])