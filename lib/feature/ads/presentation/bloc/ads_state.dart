import 'package:osmos/feature/ads/domain/entity/banner_ads.dart';

abstract class AdsState {}

class AdsInitial extends AdsState {}

class AdsLoading extends AdsState {}

class AdsLoaded extends AdsState {
  final BannerAd ad;

  AdsLoaded(this.ad);
}

class AdsError extends AdsState {
  final String message;

  AdsError(this.message);
}