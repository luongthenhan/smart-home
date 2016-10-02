app.controller('ChangeDefaultUserController', function($location) {
    var self = this;

    self.newUsername = "";
    self.password = "";

    self.changeDefaultUser = function() {
        $location.path("/home");
    }
})