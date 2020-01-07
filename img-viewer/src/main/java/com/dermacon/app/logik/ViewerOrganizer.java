package com.dermacon.app.logik;

import com.dermacon.app.dataStructures.Bookmark;
import com.dermacon.app.dataStructures.PropertyValues;
import com.dermacon.app.jfx.FXMLController;
import com.dermacon.app.worker.RenderManager;
import com.dermacon.app.worker.Renderer;

import java.io.IOException;

public class ViewerOrganizer implements Organizer {

    private final Bookmark bookmark;
//    private final PropertyValues props;
    private final Renderer renderer;
    private FXMLController fxController;

    public ViewerOrganizer(Bookmark bookmark, Renderer renderer) {
        this.bookmark = bookmark;
        this.renderer = renderer;
    }

    /**
     * Cannot be in constructor, because controller is constructed after the
     * whole construction process of this instance.
     * @param controller fx controller to display the rendered output /
     *                     preview
     */
    @Override
    public void setFXController(FXMLController controller) {
        this.fxController = fxController;
    }

    @Override
    public void prevPage() {
        System.out.println("prev");
        bookmark.decPageIdx();
        renderer.renderPageIntervall(bookmark);
        fxController.updateGui();
    }

    @Override
    public void nextPage() {
        System.out.println("next");
        bookmark.incPageIdx();
        renderer.renderPageIntervall(bookmark);
        fxController.updateGui();
    }
}
