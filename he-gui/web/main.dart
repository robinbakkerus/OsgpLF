// Copyright (c) 2017, robin. All rights reserved. Use of this source code
// is governed by a BSD-style license that can be found in the LICENSE file.

import 'package:angular2/core.dart';
import 'package:http/http.dart';
import 'package:angular2/platform/browser.dart';
import 'package:http/browser_client.dart';

import 'package:he_gui/app_component.dart';

// import 'package:he_gui/service/in_memory_data_service.dart';

// import 'package:bootjack_datepicker/bootjack_datepicker.dart';

main() {
//  bootstrap(AppComponent, [provide(Client, useClass: InMemoryDataService)]);
  bootstrap(AppComponent, [provide(Client, useFactory: () => new BrowserClient(), deps: [])]);
  // Calendar.use();


//  Table.load();
}
