import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:osmos/feature/ads/domain/entity/banner_ads.dart' show BannerAd;

import '../../data/datasource/osmos_service.dart';
import 'ads_event.dart';
import 'ads_state.dart';

class AdsBloc extends Bloc<AdsEvent, AdsState> {
  final OsmosService service;

  AdsBloc(this.service) : super(AdsInitial()) {
    on<LoadAdEvent>(_loadAd);
    on<TrackImpressionEvent>(_trackImpression);
    on<TrackClickEvent>(_trackClick);
  }

  Future<void> _loadAd(LoadAdEvent event, Emitter<AdsState> emit) async {
    emit(AdsLoading());

    try {
      final response = await service.loadAd();

      final ad = BannerAd(
        imageUrl: response["imageUrl"],
        destinationUrl: response["destinationUrl"],
        uclid: response["uclid"],
      );

      emit(AdsLoaded(ad));
    } catch (e) {
      emit(AdsError(e.toString()));
    }
  }

  Future<void> _trackImpression(
    TrackImpressionEvent event,
    Emitter<AdsState> emit,
  ) async {
    await service.trackImpression(
      uclid: event.uclid,
      position: 1,
    );
  }

  Future<void> _trackClick(
    TrackClickEvent event, 
    Emitter<AdsState> emit,
  ) async {
    await service.trackClick( uclid: event.uclid);
  }
}
