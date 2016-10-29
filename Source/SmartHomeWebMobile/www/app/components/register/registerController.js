app.controller('RegisterController', ['MainService', '$location', function(MainService, $location) {

    var ERROR_WHEN_ADD_USER = -1;
    var USERNAME_ALREADY_EXISTS = -2;
    var EMAIL_ALREADY_EXISTS = -3;

    var self = this;

    self.username = "";
    self.password = "";
    self.confirmPassword = "";
    self.fullname = "";
    self.email = "";
    self.emailExists = false;
    self.usernameExists = false;

    self.register = function() {
        MainService.register(self);
    }
}]);

app.directive('isExistedUsername', ['MainService', function (MainService) {



}])

