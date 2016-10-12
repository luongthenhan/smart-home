app.controller('LoginController', ['MainService', '$location', function(MainService, $location) {
    var self = this;

    self.username = "";
    self.password = "";

    self.login = function() {
        //MainService.hostDomain = self.username;
        $location.path("/change_default_user");
    }
}])