package edu.ucsc.cross.hse.tools.ui.resultview;

import com.be3short.jfx.event.menu.ActionDefinition;
import java.util.ArrayList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;

public interface DataView
{

	public BorderPane getMainPane();

	public void handleAction(ActionDefinition action, Object choice);

	public ArrayList<MenuItem> getMenuItems();
}
