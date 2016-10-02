app.controller('LoginController', function($location) {
    var self = this;

    self.username = "";
    self.password = "";

    self.login = function() {
        $location.path("/change_default_user");
    }
})