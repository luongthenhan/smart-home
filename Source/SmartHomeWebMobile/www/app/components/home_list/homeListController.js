app.controller('HomeListController', ['MainService', '$route', function (MainService, $route) {

    var self = this;

    self.homes = [];
    self.newHomeName = "";
    self.newHomeAddress = "";
    self.newHomeDescription = "";

    self.init = function() {
        MainService.getHomes(self);
        console.log(self.homes);
    }

    self.addHome = function() {
        var newHome = {};
        newHome.name = self.newHomeName;
        newHome.address = self.newHomeAddress;
        newHome.description = self.newHomeDescription;
        MainService.addHome(newHome);
        $route.reload();
    }

}])