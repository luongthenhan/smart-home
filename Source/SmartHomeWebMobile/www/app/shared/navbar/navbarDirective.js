app.directive("navbar", ['MainService', function(MainService) {
    return {
        restrict: "E",
        templateUrl: "app/shared/navbar/navbarView.html",
        controllerAs: 'navBarCtrl',
        controller: function($scope, $route, $location) {
            var self = this;

            self.home = null;
            self.modes = [];
            self.activatedMode = null;

            self.init = function() {
                if (MainService.selectedHome != null) {
                    self.home = MainService.selectedHome;
                    self.modes = MainService.selectedHome.modes;
                    self.activatedMode = MainService.selectedHome.currentMode;
                }
            }

            self.toHomeList = function() {
                MainService.selectedHome = null;
                $location.path("/homes");
            }

            self.changeMode = function(mode) {
                MainService.activateMode(mode);
                $route.reload();
            }

            self.deleteMode = function(mode) {
                if (self.activatedMode.id == mode.id) {
                    console.log("try to delete activated mode");
                    var previousMode = self.modes[self.modes.indexOf(mode) - 1]
                    MainService.activateMode(previousMode);
                    MainService.selectedMode = previousMode;
                }
                MainService.deleteMode(mode);
                $route.reload();
            }
        }
    }
}])
