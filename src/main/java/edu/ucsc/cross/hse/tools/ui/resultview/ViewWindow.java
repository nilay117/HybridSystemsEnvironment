package edu.ucsc.cross.hse.tools.ui.resultview;

import com.be3short.jfx.event.menu.ActionDefinition;
import com.be3short.jfx.event.menu.ActionEventHandler;
import edu.ucsc.cross.hse.core.exe.operator.HybridEnvironment;
import edu.ucsc.cross.hse.core.obj.data.DataSet;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.geometry.Orientation;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jfree.graphics2d.svg.SVGGraphics2D;
import org.jfree.graphics2d.svg.SVGUtils;

public class ViewWindow extends ActionEventHandler
{

	public BorderPane getMainPane()
	{
		return mainPane;
	}

	private DataSet environmentOperator;
	private BorderPane mainPane;
	private SplitPane sectionalPane;
	private MenuBar mainMenu;
	private HashMap<ViewPane, Boolean> childPanes;

	public ViewWindow(DataSet environment_operator)
	{
		super(VisualizationWindowActions.CLOSE.rootItems());
		environmentOperator = environment_operator;
		mainPane = new BorderPane();
		Platform.runLater(() ->
		{
			try
			{
				initialize();
			} catch (Exception ex)
			{
				// ex.printStackTrace();
			}
		});

	}

	private void setupMenu()
	{
		mainMenu = new MenuBar();

		for (ActionDefinition def : VisualizationWindowActions.CLOSE.rootItems())
		{
			mainMenu.getMenus().add((Menu) getMenuItemFromDefinition(def));
		}
		mainPane.setTop(mainMenu);
	}

	public void captureSVG(File f)
	{
		// mainPane.setTop(null);
		hideAllMenus();
		HashMap<ViewPane, Double[]> sizes = new HashMap<ViewPane, Double[]>();
		double width = mainPane.widthProperty().get();
		double height = mainPane.heightProperty().get();
		// Integer w = 1000;
		// Integer h = 1000;

		double scaleWidth = 1.0;// width;// / w;
		double scaleHeight = 1.0;// height / h;
		SVGGraphics2D graphics = new SVGGraphics2D(((int) (scaleWidth * width)), ((int) (scaleHeight * height)));// 1000,
																													// 1000);
		for (ViewPane pane : childPanes.keySet())
		{

			double widthPercent = scaleWidth * (pane.getMainPane().widthProperty().get());
			double heightPercent = scaleHeight * (pane.getMainPane().heightProperty().get());
			double xPos = scaleWidth * pane.getMainPane().getLocalToSceneTransform().getTx();
			double yPos = scaleHeight * pane.getMainPane().getLocalToSceneTransform().getTy();
			Rectangle r = new Rectangle((int) xPos, (int) yPos, (int) widthPercent, (int) heightPercent);
			pane.getChart().drawChartSVG(graphics, r);

		}
		try
		{
			SVGUtils.writeToSVG(f, graphics.getSVGElement());
		} catch (IOException e)
		{
		}
		restoreForceClosedMenus();

	}

	private void initialize()
	{

		childPanes = new HashMap<ViewPane, Boolean>();

		sectionalPane = new SplitPane();
		sectionalPane.setOrientation(Orientation.VERTICAL);// .HORIZONTAL);
		ViewPane chart = new ViewPane(environmentOperator, this, sectionalPane);
		childPanes.put(chart, true);
		// SplitPane p = new SplitPane();
		// p.setOrientation(Orientation.HORIZONTAL);
		// p.getItems().add(chart.getMainPane());
		// chart.setParentPane(p);
		sectionalPane.getItems().add(chart.getMainPane());
		mainPane.setCenter(sectionalPane);
		setupMenu();
		// sectionalPane.setBackground(
		// new Background(new BackgroundFill(Color.TRANSPARENT,
		// CornerRadii.EMPTY, new Insets(0, 0, 0, 0))));
		// mainPane.setBackground(
		// new Background(new BackgroundFill(Color.TRANSPARENT,
		// CornerRadii.EMPTY, new Insets(0, 0, 0, 0))));
	}

	public void loadP(String... filt)
	{

	}

	public ViewPane split(ViewPane loc, Orientation orientation)
	{

		ViewPane chart = new ViewPane(environmentOperator, this, loc.getParentPane());// LINE_CHART);
		Orientation rotate = Orientation.HORIZONTAL;
		if (orientation.equals(rotate))
		{
			rotate = Orientation.VERTICAL;
		}
		Integer ind = getItemIndex(loc.getMainPane(), loc.getParentPane());
		if (loc.getParentPane().getOrientation().equals(orientation))
		{
			// Orientation rotate = Orientation.HORIZONTAL;
			// if (orientation.equals(rotate))
			// {
			// rotate = Orientation.VERTICAL;
			// }
			SplitPane p = new SplitPane();
			p.setOrientation(orientation);// rotate);// loc.getParentPane().getOrientation());
			// loc.getParentPane().setOrientation(orientation);
			chart.setParentPane(p);
			ViewPane chart2 = new ViewPane(environmentOperator, this, p);// loc.getParentPane());// LINE_CHART);
			// parent.getItems().clear();
			loc.getParentPane().getItems().remove(loc.getMainPane());
			p.getItems().addAll(chart2.getMainPane(), chart.getMainPane());
			childPanes.put(chart2, true);
			// p.getItems().addAll(chart.getMainPane());
			// loc.getParentPane().getItems().addAll(loc.getMainPane(), p);// chart.getMainPane());//
			loc.getParentPane().getItems().add(ind, p);// chart.getMainPane()); // .remove(loc.getMainPane());

		} else
		{
			SplitPane parent = loc.getParentPane();
			SplitPane p = new SplitPane();
			p.setOrientation(orientation);// rotate);// loc.getParentPane().getOrientation());
			// p.setOrientation(rotate);
			parent.setOrientation(rotate);
			ViewPane chart2 = new ViewPane(environmentOperator, this, p);// loc.getParentPane());// LINE_CHART);
			// parent.getItems().clear();
			parent.getItems().remove(loc.getMainPane());
			p.getItems().addAll(chart2.getMainPane(), chart.getMainPane());
			// parent.getItems().clear();
			// parent.setOrientation(rotate);
			parent.getItems().add(ind, p);// chart.getMainPane());
			childPanes.put(chart2, true);
			chart.setParentPane(p);

		}
		childPanes.remove(loc);
		childPanes.put(chart, true);
		return chart;
	}

	public void hideAllMenus()
	{

		for (ViewPane pane : childPanes.keySet())
		{
			childPanes.put(pane, (pane.getMainPane().getBottom() == null));
			pane.displayMenu(false);
		}
	}

	private void restoreForceClosedMenus()
	{
		for (ViewPane pane : childPanes.keySet())
		{
			pane.displayMenu(!childPanes.get(pane));

		}
	}

	private void viewPaneMenus(boolean visible)
	{
		for (ViewPane pane : childPanes.keySet())
		{
			childPanes.put(pane, visible);
			pane.displayMenu(visible);
		}
	}

	private Integer getItemIndex(Object obj, SplitPane pane)
	{
		Integer ind = 0;

		for (Object o : pane.getItems())
		{
			if (o.equals(obj))
			{
				return ind;
			}
			ind = ind + 1;
		}
		return ind;
	}

	@Override
	public Object respondToEvent(Object event)
	{
		try
		{
			VisualizationWindowActions action = (VisualizationWindowActions) this.menuItemSelected.get();

			switch (action)
			{
			case CLOSE:
			{
				return action;

			}
			case REFRESH:
			{
				initialize();
				break;
			}
			case SHOW_PANE_MENUS:
				viewPaneMenus(true);
				break;
			case HIDE_PANE_MENUS:
				viewPaneMenus(false);
				break;
			case SAVE_WINDOW_SVG:
			{
				captureSVG(new FileChooser().showSaveDialog(new Stage()));

				break;
			}
			case SAVE_ENVIRONMENT:
			{
				environmentOperator.getManager()
				.save((new FileChooser().showSaveDialog(new Stage())).getAbsolutePath() + ".hse");
				break;
			}

			case LOAD_ENVIRONMENT:
				HybridEnvironment env = HybridEnvironment.load(new FileChooser().showOpenDialog(new Stage()));
				if (env != null)
				{
					environmentOperator = env.manager.getDataCollector();
				}
				initialize();
				break;
			}
		} catch (Exception e)
		{
			try
			{
			} catch (Exception notSave)
			{

			}
		}
		this.menuItemSelected.set(null);
		return null;
	}

	public void captureImage(File output)
	{
		hideAllMenus();

		restoreForceClosedMenus();
	}

}
