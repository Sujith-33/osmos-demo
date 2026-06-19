import 'package:flutter/material.dart';
import 'package:osmos/feature/ads/presentation/paages/ads_pages.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return  MaterialApp(
      debugShowCheckedModeBanner: false,
      home: AdsPage(),
    );
  }
}
