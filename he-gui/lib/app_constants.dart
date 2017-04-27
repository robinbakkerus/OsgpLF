import 'event/event_bus.dart';

class AppConstants {

  static List<String> findEventCategories() {
    return ['STANDARD_EVENT_LOG', 'FRAUD_DETECTION_LOG',
      'COMMUNICATION_SESSION_LOG', 'M_BUS_EVENT_LOG'];
  }

  static EventBus eventBus = new EventBus();

}
