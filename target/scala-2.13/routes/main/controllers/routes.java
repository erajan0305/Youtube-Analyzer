// @GENERATOR:play-routes-compiler
// @SOURCE:/home/akash/Rajan/SOEN 6441/youtube-analyzer/conf/routes
// @DATE:Sat Oct 24 10:57:34 MDT 2020

package controllers;

import router.RoutesPrefix;

public class routes {
  
  public static final controllers.ReverseYoutubeAnalyzerController YoutubeAnalyzerController = new controllers.ReverseYoutubeAnalyzerController(RoutesPrefix.byNamePrefix());
  public static final controllers.ReverseAssets Assets = new controllers.ReverseAssets(RoutesPrefix.byNamePrefix());

  public static class javascript {
    
    public static final controllers.javascript.ReverseYoutubeAnalyzerController YoutubeAnalyzerController = new controllers.javascript.ReverseYoutubeAnalyzerController(RoutesPrefix.byNamePrefix());
    public static final controllers.javascript.ReverseAssets Assets = new controllers.javascript.ReverseAssets(RoutesPrefix.byNamePrefix());
  }

}
