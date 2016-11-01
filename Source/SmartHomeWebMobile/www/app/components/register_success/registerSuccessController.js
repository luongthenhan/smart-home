app.controller('RegisterSuccessController', ['MainService', '$location', function(MainService, $location) {
    var self = this;

    self.goToLogin = function() {
        $location.path("/login");
    }
}])