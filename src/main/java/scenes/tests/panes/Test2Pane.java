package scenes.tests.panes;

import fx.dialog.SrcDstListDialog;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static util.Util.setPaneBackground;

public class Test2Pane extends HBox {
    public Test2Pane() {
        super();

        setId("Test2Pane");

        setPaneBackground(this);

        setupTest();
        runTest();
    }

    private  void  setupTest() {
        setPadding(new Insets(5));
        setSpacing(5);
    }

    private void runTest() {
        List<String> srcList = new ArrayList<>();
        srcList.add("src1");
        srcList.add("src2");
        srcList.add("src");

        List<String> dstList = new ArrayList<>();
        dstList.add("dst1");
        dstList.add("dst2");

        SrcDstListDialog<String> dialog = new SrcDstListDialog<>("This is the title", "Determine the events to be shown or hidden", "Filter", "SrcLabel", "DstLabel", srcList, dstList);

        Optional<List<String>> result = dialog.showAndWait();
        result.ifPresent(list -> {
            System.out.println("Received something");
        });
    }

    private void testMidi() {

    }
}
