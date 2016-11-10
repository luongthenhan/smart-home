app.directive("navbar", ['MainService', function(MainService) {
    return {
        restrict: "E",
        templateUrl: "app/shared/navbar/navbarView.html",
        controllerAs: 'navBarCtrl',
        controller: function($scope, $location) {
            var self = this;

            self.modes = [];
            self.selectedMode = null;

            self.init = function() {
                MainService.navBarCtrl = self;
            }

            self.toHomeList = function() {
                $location.path("/homes");
            }

            self.changeMode = function(mode) {
                $location.path("/home");
            }
        }
    }
}])
