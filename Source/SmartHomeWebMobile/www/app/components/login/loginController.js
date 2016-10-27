app.controller('LoginController', ['MainService', '$location', function(MainService, $location) {
    var self = this;

    self.username = "";
    self.password = "";
    self.hostDomain = "";

    self.login = function() {
        MainService.hostDomain = self.hostDomain;
        //$location.path("/change_default_user");
        MainService.login(self.username, self.password, self);
    }

    self.redirectToHome = function() {
        $location.path("/home");
    }
}])