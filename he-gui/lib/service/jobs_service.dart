import 'dart:async';
import 'dart:convert';

import 'package:angular2/core.dart';
import 'package:http/http.dart';

import '../model/job.dart';
import '../model/recipe.dart';
import '../model/response_msg.dart';
import '../model/he_statistics.dart';
import '../model/devicegroup_msg.dart';
import '../app_constants.dart';
import '../event/app_events.dart';

@Injectable()
class JobsService {
  static final _headers = {'Content-Type': 'application/json'};
//  static const _baseUrl = 'app/jobs'; // URL to web API
  static const _baseUrl = 'http://localhost:4567/';

  final Client _http;

  JobsService(this._http);

  Future<List<Job>> getJobs() async {
    try {
      final response = await _http.get(_baseUrl + 'jobs');
      return _extractData(response)
          .map((value) => new Job.fromJson(value))
          .toList();
    } catch (e) {
      throw _handleError(e);
    }
  }

  // void onDataLoaded(String responseText) {
  //   var jsonString = responseText;
  // }

  dynamic _extractData(Response resp) => JSON.decode(resp.body)['data'];

  Exception _handleError(dynamic e) {
    print(e); // for demo purposes only
    return new Exception('Server error; cause: $e');
  }

  Future<Job> getJob(int id) async {
    try {
      final response = await _http.get(_baseUrl + 'job/' + id.toString());
      return new Job.fromJson(_extractData(response));
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<Null> addJob(Job job) async {
    try {
      String url = _baseUrl + "addJob";
      _http.post(url, headers: _headers, body: job.toJson().toString());
      AppConstants.eventBus.fire(new AddJobEvent(job));
      // return new Job.fromJson(_extractData(response));
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<List<Recipe>> getRecipes() async {
    try {
      final response = await _http.get(_baseUrl + 'recipes');
      return _extractData(response)
          .map((value) => new Recipe.fromJson(value))
          .toList();
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<Recipe> getRecipe(int id) async {
    try {
      final response = await _http.get(_baseUrl + 'recipe/' + id.toString());
      return new Recipe.fromJson(_extractData(response));
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<Null> addRecipe(Recipe recipe) async {
    try {
      String url = _baseUrl + "addRecipe";
      _http.post(url, headers: _headers, body: recipe.toJson().toString());
      AppConstants.eventBus.fire(new AddRecipeEvent(recipe));
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<Null> resetStatistics() async {
    try {
      await _http.get(_baseUrl + 'resetStatistics');
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<HeStatistics> getStatistics() async {
    try {
      final response = await _http.get(_baseUrl + 'statistics');
      return new HeStatistics.fromJson(_extractData(response));
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<ResponseMsg> getResponse(String correlId) async {
    try {
      final response = await _http.get(_baseUrl + 'response/' + correlId);
      return new ResponseMsg.fromJson(_extractData(response));
    } catch (e) {
      throw _handleError(e);
    }
  }

  Future<List<DeviceGroup>> getDeviceGroups() async {
    try {
      final response = await _http.get(_baseUrl + 'deviceGroups');
      return _extractData(response)
          .map((value) => new DeviceGroup.fromJson(value))
          .toList();
    } catch (e) {
      throw _handleError(e);
    }
  }
}
