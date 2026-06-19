import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:osmos/core/visibility/add_visibility.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:visibility_detector/visibility_detector.dart';
import '../../data/datasource/osmos_service.dart';
import '../bloc/ads_bloc.dart';
import '../bloc/ads_event.dart';
import '../bloc/ads_state.dart';

class AdsPage extends StatelessWidget {
  AdsPage({super.key});

  final tracker = AdVisibilityTracker();

  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (_) => AdsBloc(OsmosService()),
      child: Scaffold(
        appBar: AppBar(title: const Text("Osmos Demo")),
        body: BlocBuilder<AdsBloc, AdsState>(
          builder: (context, state) {
            if (state is AdsLoading) {
              return const Center(child: CircularProgressIndicator());
            }

            if (state is AdsError) {
              return Center(
                child: Padding(
                  padding: const EdgeInsets.all(24),
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      const Icon(
                        Icons.error_outline,
                        size: 48,
                        color: Colors.redAccent,
                      ),
                      const SizedBox(height: 12),
                      const Text(
                        "Ad not available",
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.w600,
                        ),
                        textAlign: TextAlign.center,
                      ),
                      const SizedBox(height: 8),
                      const Text(
                        "Something went wrong. Please try again.",
                        textAlign: TextAlign.center,
                      ),
                      const SizedBox(height: 16),
                      ElevatedButton(
                        onPressed: () {
                          context.read<AdsBloc>().add(LoadAdEvent());
                        },
                        child: const Text("Load Again"),
                      ),
                    ],
                  ),
                ),
              );
            }
            if (state is AdsLoaded) {
              return Center(
                child: VisibilityDetector(
                  key: const Key("ad"),
                  onVisibilityChanged: (info) {
                    if (tracker.shouldTrack(info.visibleFraction)) {
                      context.read<AdsBloc>().add(
                        TrackImpressionEvent(state.ad.uclid),
                      );
                    }
                  },
                  child: GestureDetector(
                    onTap: () async {
                      context.read<AdsBloc>().add(
                        TrackClickEvent(state.ad.uclid),
                      );

                      if (state.ad.destinationUrl.isNotEmpty) {
                        await launchUrl(Uri.parse(state.ad.destinationUrl));
                      }
                    },
                    child: SizedBox(
                      width: 250,
                      height: 250,
                      child: Image.network(
                        state.ad.imageUrl,
                        fit: BoxFit.cover,
                        loadingBuilder: (context, child, loadingProgress) {
                          if (loadingProgress == null) {
                            return child;
                          }

                          return const Center(
                            child: CircularProgressIndicator(),
                          );
                        },
                        errorBuilder: (context, error, stackTrace) {
                          return Container(
                            color: Colors.grey.shade100,
                            child: const Center(
                              child: Padding(
                                padding: EdgeInsets.all(16),
                                child: Text(
                                  "Unable to show ad image",
                                  textAlign: TextAlign.center,
                                ),
                              ),
                            ),
                          );
                        },
                      ),
                    ),
                  ),
                ),
              );
            }

            return Center(
              child: ElevatedButton(
                onPressed: () {
                  context.read<AdsBloc>().add(LoadAdEvent());
                },
                child: const Text("Load Ad"),
              ),
            );
          },
        ),
      ),
    );
  }
}
