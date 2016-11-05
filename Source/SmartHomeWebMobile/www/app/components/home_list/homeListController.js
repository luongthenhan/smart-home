app.controller('HomeListController', ['MainService', function (MainService) {

    var self = this;

    self.homes = [];
    self.newHomeName = "";
    self.newHomeAddress = "";
    self.newHomeDescription = "";

    self.init = function() {
        MainService.getHomes(self);
    }

    self.addHome = function() {
        MainService.addHome(self);
    }

}])