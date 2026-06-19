abstract class AdsEvent {}

class LoadAdEvent extends AdsEvent {}

class TrackImpressionEvent extends AdsEvent {
  final String uclid;

  TrackImpressionEvent(this.uclid);
}

class TrackClickEvent extends AdsEvent {
  final String uclid;

  TrackClickEvent(this.uclid);
}
