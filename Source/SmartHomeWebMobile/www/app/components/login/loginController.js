app.controller('LoginController', ['MainService', '$location', function(MainService, $location) {
    var self = this;

    self.username = "";
    self.password = "";
    self.hostDomain = "https://192.168.1.129:8443/hcmut/api/";

    self.login = function() {

        MainService.hostDomain = self.hostDomain;
        MainService.login(self.username, self.password, self);
    }

    self.redirectToHome = function() {
        //$location.path("/home");
        $location.path("/homes");
    }

    self.register = function() {
        MainService.hostDomain = self.hostDomain;
        $location.path("/register");
    }
}])