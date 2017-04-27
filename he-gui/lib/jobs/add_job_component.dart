// import 'dart:developer';
import 'dart:async';
import 'package:angular2/core.dart';
import 'package:angular2/router.dart';
// import 'package:angular2/platform/common.dart';
import 'package:angular2_components/angular2_components.dart';

import '../service/jobs_service.dart';
import '../pipe/app_pipes.dart';
import '../model/job.dart';
import '../model/action/dlms_action_msg.dart';
import '../model/recipe.dart';
import '../model/devicegroup_msg.dart';
import '../action/action_component.dart';
import '../util/validation_utils.dart';

@Component(
    selector: 'add-job',
    templateUrl: 'add_job_component.html',
    styleUrls: const ['../app.css'],
    pipes: const[IntToDateTime],
    directives: const [materialDirectives, ActionComponent],
    providers: const [materialProviders]
)

class AddJobComponent implements OnInit {
  Job job = new Job.newJob();
  List<Recipe> recipes;
  List<DeviceGroup> deviceGroups;

  final JobsService _jobsService;
  final Router _router;

  DlmsActionMsg selectedAction;
  bool startDirect = true;
  bool showMarkers = false;

  AddJobComponent(this._jobsService, this._router);

  void ngOnInit() {
    getRecipes();
    getDeviceGroups();
  }

  Future<Null> getRecipes() async => this.recipes = await _jobsService.getRecipes();
  Future<Null> getDeviceGroups() async => this.deviceGroups = await _jobsService.getDeviceGroups();

  //-- group

  // String groupName;
  bool showGroups = false;
  void toggleShowGroups() {showGroups = !showGroups;}

  void selectGroup(DeviceGroup grp) {
    toggleShowGroups();
    this.job.deviceGroupId = grp.id;
    this.job.deviceGroup = grp.name;
  }

  // String recipeName = "";
  bool showRecipes = false;
  void toggleShowRecipes() {showRecipes = !showRecipes;}

  void selectRecipe(Recipe recipe) {
    toggleShowRecipes();
    copyActionFromRecipe(recipe);
    this.job.name = recipe.name;
    this.job.recipeId = recipe.id;
    this.job.recipe = recipe.name;
  }

  void copyActionFromRecipe(Recipe recipe) {
    if (recipe.actions != null) {
      job.actions = new List();
      job.actions.addAll(recipe.actions);
    }
  }

  //-- actions
  bool showEditTask = false;

  void editAction(DlmsActionMsg dlmsAction) {
    selectedAction = dlmsAction;
    showEditTask = !showEditTask;
  }

  void closeActionDialog() {
    showEditTask = false;
  }

  void startJob() {
    this.showMarkers = true;
    if (this.formIsValid()) {
      _jobsService.resetStatistics();
      _jobsService.addJob(job);
      gotoStatistics();
    }
  }

  void cancel() {
    gotoJobs();
  }

  void gotoJobs() {
    _router.navigate([
      'Jobs'
    ]);
  }

  void gotoStatistics() {
    _router.navigate([
      'Statistics'
    ]);
  }

  Map<String, bool> recipeClass() => ValidationUtils.ngClass(job.recipe, showMarkers);
  Map<String, bool> nameClass() => ValidationUtils.ngClass(job.name, showMarkers);
  Map<String, bool> groupClass() => ValidationUtils.ngClass(job.deviceGroup, showMarkers);
  Map<String, bool> actionClass(DlmsActionMsg dlmsAction) => ValidationUtils.actionClass(dlmsAction);

  bool disableStartBtn() => showMarkers && !formIsValid();

  bool formIsValid() {
    bool actionsValid = true;
    if (job.actions != null) {
      for (var dlmsAction in job.actions){
        if (!dlmsAction.action.isValid()) actionsValid = false;
      }
    }

    return isValid(this.job.recipe) && isValid(this.job.deviceGroup)
      && isValid(this.job.name) && actionsValid;
  }

  bool isValid(String s) => s != null && !s.isEmpty;
}
