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
            self.selectedEditMode = null;
            self.beforeEditMode = null;

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

            self.toManageCustomScripts = function() {
                $location.path("/manage_custom_scripts");
            }

            self.changeMode = function(mode) {
                MainService.activateMode(mode);
                $route.reload();
            }

            self.deleteMode = function() {
                if (self.activatedMode.id == self.selectedEditMode.id) {
                    console.log("try to delete activated mode");
                    var previousMode = self.modes[self.modes.indexOf(self.selectedEditMode) - 1]
                    MainService.activateMode(previousMode);
                    MainService.selectedMode = previousMode;
                }
                MainService.deleteMode(self.selectedEditMode);
                $route.reload();
            }

            self.resetModeValue = function() {
                self.selectedEditMode.name = self.beforeEditMode.name;
            }

            self.showDeleteModeModal = function() {
                $("#navbar-delete-mode-modal").modal('show');
            }

            self.showEditModeModal = function(mode) {
                self.selectedEditMode = mode;
                self.beforeEditMode = angular.copy(self.selectedEditMode);
                $("#navbar-edit-mode-modal").modal('show');
            }

            self.updateMode = function() {
                console.log("Try to update mode: " + self.selectedEditMode.name);
                MainService.updateMode(self.selectedEditMode);
            }
        }
    }
}])
