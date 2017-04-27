// import 'dart:developer';
import 'dart:async';
import 'package:angular2/core.dart';
import 'package:angular2/router.dart';
// import 'package:angular2/platform/common.dart';
import 'package:angular2_components/angular2_components.dart';

import '../service/jobs_service.dart';
import '../pipe/app_pipes.dart';
import '../model/recipe.dart';
import '../model/action/dlms_action_msg.dart';
import '../model/action/abstract_action.dart';
import '../enum/request_type.dart';
import '../action/action_component.dart';
import '../util/validation_utils.dart';

@Component(
    selector: 'recipe-detail',
    templateUrl: 'recipe_detail_component.html',
    styleUrls: const ['../app.css'],
    pipes: const[IntToDateTime, RequestTypePrettyPrint],
    directives: const [materialDirectives, ActionComponent],
    providers: const [materialProviders]
)

class RecipeDetailComponent implements OnInit {
  Recipe recipe = new Recipe.newRecipe();

  final JobsService _jobsService;
  final Router _router;
  final RouteParams _routeParams;


  DlmsActionMsg selectedAction;
  bool startDirect = true;
  bool showMarkers = false;

  //-- actions
  bool showEditTask = false;
  List<RequestType> allActions;

  RecipeDetailComponent(this._jobsService, this._router, this._routeParams);

  Future<Null>  ngOnInit() async {
    var _id = _routeParams.get('id');
    var id = int.parse(_id ?? '', onError: (_) => null);
    if (id != null) recipe = await (_jobsService.getRecipe(id));
    allActions = RequestType.values;
  }


  void editAction(DlmsActionMsg dlmsAction) {
    selectedAction = dlmsAction;
    showEditTask = !showEditTask;
  }

  void closeActionDialog() {
      showEditTask = false;
  }

  Future<Null> saveRecipe() async {
    this.showMarkers = true;

    if (formIsValid()) {
      await _jobsService.addRecipe(recipe);
      gotoRecipes();
    }
  }

  void cancel() {
    gotoRecipes();
  }

  void gotoRecipes() {
    _router.navigate([
      'Recipes'
    ]);
  }

  Map<String, bool> nameClass() => ValidationUtils.ngClass(recipe.name, showMarkers);
  Map<String, bool> recipeClass(DlmsActionMsg dlmsAction) => ValidationUtils.actionClass(dlmsAction);
  Map<String, bool> actionListClass() => ValidationUtils.listClass(this.recipe.actions.length, showMarkers);

  bool disableSaveBtn() {
    if (!this.showMarkers) {
      return false; // de eerste keer kun je klikken, maar gaat aktie niet door als niet valide
    }

    return !isValid(recipe.name);
  }

  bool formIsValid() {
    return isValid(this.recipe.name) && this.recipe.actions.length > 0;
  }

  bool isValid(String s) => s != null && !s.isEmpty;

  bool showSelectAction = false;

  void addAction() { showSelectAction = true; }

  void selectAction(RequestType reqType) {
    showSelectAction = false;
    AbstractAction actionMsg = DlmsActionMsg.makeNewAction(reqType);
    recipe.actions.add(new DlmsActionMsg(reqType.toString(), actionMsg));

    if (this.recipe.name == null || this.recipe.name.isEmpty) {
      this.recipe.name = actionMsg.description();
    }
  }

  Map<String, bool> actionClass(DlmsActionMsg dlmsAction) => ValidationUtils.actionClass(dlmsAction);

}
