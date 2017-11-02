package edu.cross.ucsc.hse.core.chart;

import com.be3short.io.format.FileSpecifications;
import com.be3short.io.format.ImageFormat;
import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import de.erichseifert.vectorgraphics2d.eps.EPSProcessor;
import de.erichseifert.vectorgraphics2d.intermediate.CommandSequence;
import de.erichseifert.vectorgraphics2d.svg.SVGProcessor;
import de.erichseifert.vectorgraphics2d.util.PageSize;
import edu.ucsc.cross.hse.core.data.DataSeries;
import edu.ucsc.cross.hse.core.environment.Environment;
import edu.ucsc.cross.hse.core.io.Console;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingNode;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ChartView
{

	private static Integer closeablePlots = 0;
	private BorderPane addOnPane;
	private BorderPane editorPane;
	private Environment env;
	// private JPanel legendPanel;
	private BorderPane mainPane;
	private FileSpecifications<ImageFormat> output;
	private ArrayList<JPanel> panels;
	private Chart plot;
	private Pane plotPane;
	private ArrayList<String> elementOrder;
	private ArrayList<SubChartView> plots;

	public void captureGraphic(File f, ImageFormat format)
	{
		double width = plotPane.widthProperty().get();
		double height = plotPane.heightProperty().get() + plot.measureFont();
		BufferedImage bi = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
		prepareBackgroundsForGraphicsCapture(format.needsBackground);// format.needsBackground);
		Graphics2D graphics = prepareVectorGraphics(format.image, bi);
		graphics = paintGraphics(graphics);
		createGraphicFile(f, format, graphics, bi);

	}

	public void displayMenu(boolean visible)
	{
		if (visible)

		{
			// mainPane.setBottom(settingsMenu);
		} else
		{
			mainPane.setBottom(null);
		}

	}

	public ArrayList<String> getElementOrder()
	{
		return elementOrder;
	}

	private ArrayList<String> initializeElementOrder()
	{
		ArrayList<String> dataNames = new ArrayList<String>();
		for (DataSeries<Double> dataSeries : env.getData().getGlobalStateData())
		{
			dataNames.add(getLegendLabel(dataSeries, dataNames));
		}
		Collections.sort(dataNames);
		return dataNames;

	}

	public String getLegendLabel(DataSeries<Double> data, ArrayList<String> names)
	{

		String label = data.getParentName();
		if (this.env.getData().getStates().containsKey(data.getParentID()))
		{
			return this.env.getData().getStates().get(data.getParentID());
		}
		String labelBase = label;
		int append = 1;
		label = labelBase + "(" + append + ")";
		while (names.contains(label))
		{
			append++;
			label = labelBase + "(" + append + ")";
		}
		this.env.getData().getStates().put(data.getParentID(), label);
		return label;

	}

	public void generatePlots()
	{
		elementOrder = initializeElementOrder();
		SubChartView pf = null;
		HashMap<Integer, Integer[][]> dimensions = plot.getChartLocations();
		for (Integer paneIndex : dimensions.keySet())
		{
			pf = null;
			if (env == null)
			{
				pf = new SubChartView(createSubChartPane(dimensions.get(paneIndex), paneIndex, false), plot, paneIndex,
				this);
			} else
			{
				if (plot.sub(paneIndex).getyDataSelection() != null)
				{
					pf = new SubChartView(env.getData(),
					createSubChartPane(dimensions.get(paneIndex), paneIndex, false),
					plot.sub(paneIndex).getyDataSelection(), plot, paneIndex, this);
				} else
				{
					pf = new SubChartView(env.getData(),
					createSubChartPane(dimensions.get(paneIndex), paneIndex, false), plot, paneIndex, this);
				}
			}
			plots.add(pf);
			plotPane.getChildren().add(pf.getPane());
			plotPane.setMaxSize(plot.getWidth(), plot.getHeight());
		}

	}

	public BorderPane getMainPane()
	{
		return mainPane;
	}

	private BorderPane createSizedAndTranslatedPane(Integer[][] dimensions, Integer chart_index, Double x_min,
	Double x_max, Double y_min, Double y_max, boolean label)
	{

		Double widthGrid = plot.getWidth() / dimensions[0].length;
		Double heightGrid = (plot.getHeight(true)) / dimensions.length;
		Double x = x_min * widthGrid;
		Double y = (y_min * heightGrid);

		Double w = (1 + (x_max - x_min)) * widthGrid;
		Double h = (1 + (y_max - y_min)) * heightGrid;// - plot.measureFont();

		BorderPane pan = new BorderPane();

		pan.setMaxSize(w, h);
		pan.setMinSize(w, h);
		pan.setPrefSize(w, h);
		pan.setTranslateX(x);
		pan.translateYProperty().set(y);

		return pan;
	}

	public boolean menuVisible()
	{
		return (mainPane.getBottom() != null);
	}

	private void createGraphicFile(File f, ImageFormat format, Graphics2D graphics, BufferedImage bi)
	{
		try
		{
			File extended = f;
			if (!extended.getName().contains("."))
			{
				extended = new File(f.getAbsoluteFile() + format.extension);
			}
			if (format.image)

			{
				ImageIO.write(bi, format.name, extended);
			} else
			{
				CommandSequence c = ((VectorGraphics2D) graphics).getCommands();
				Document doc = null;
				if (format.equals(ImageFormat.SVG))
				{
					doc = (new SVGProcessor()).getDocument(c,
					new PageSize(new Rectangle(bi.getWidth(), bi.getHeight())));
				} else if (format.equals(ImageFormat.EPS))
				{
					doc = (new EPSProcessor()).getDocument(c,
					new PageSize(new Rectangle(bi.getWidth(), bi.getHeight())));
				}
				doc.writeTo(new FileOutputStream(extended));
			}
		} catch (

		Exception e)
		{
		}
	}

	private void createSwingContent()
	{
		final SwingNode swingNode = new SwingNode();
		//
		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{

				JLabel label = new JLabel(" " + plot.getMainTitle() + " ");
				Font font = plot.getLabelFont(LabelType.MAIN_TITLE);
				label.setFont(font);

				JPanel panel = new JPanel();
				panel.add(label, BorderLayout.CENTER);
				// panel.setAlignmentX(JPanel.CENTER_ALIGNMENT);

				swingNode.setContent(panel);// panel);
				panels.add(panel);
			}
		});
		addOnPane.setTop(swingNode);
	}

	private void getOutput(FileSpecifications<ImageFormat> file_specs, Stage stage)
	{
		this.output = file_specs;
		File out = output.getLocation(true);

		Task<Integer> task = new Task<Integer>()
		{

			@Override
			protected Integer call() throws Exception
			{
				final BooleanProperty success = new SimpleBooleanProperty(false);
				Long attemptTime = System.currentTimeMillis() + 10000;
				while (!success.getValue())
				{
					if (attemptTime <= System.currentTimeMillis())
					{
						try
						{

							captureGraphic(out, output.getFormat());// .getMatch(action.label));

							success.setValue(true);

						} catch (Exception e)
						{
							e.printStackTrace();
							// Console.error("Unable to create output plot: " + output.toString());
						}
						// your code here

						// }
						// }, (int) 500, (int) 500);
						attemptTime = System.currentTimeMillis() + 10000;
					}
				}
				return 0;
			}

			@Override
			protected void succeeded()
			{
				super.succeeded();
				stage.close();
				closeablePlots--;
			}

		};
		new Thread(task).start();

	}

	private BorderPane createSubChartPane(Integer[][] dimensions, Integer chart_index, boolean label)
	{
		Integer maxX = 0;
		Integer maxY = 0;
		Integer minX = dimensions[0].length;
		Integer minY = dimensions.length;
		for (int rowIndex = 0; rowIndex < dimensions.length; rowIndex++)
		{
			for (int colIndex = 0; colIndex < dimensions[0].length; colIndex++)
			{
				if (dimensions[rowIndex][colIndex] >= 0)
				{
					if (colIndex < minX)
					{
						minX = colIndex;
					}
					if (colIndex > maxX)
					{
						maxX = colIndex;
					}

					if (rowIndex < minY)
					{
						minY = rowIndex;
					}
					if (rowIndex > maxY)
					{
						maxY = rowIndex;
					}
				}
			}
		}
		return createSizedAndTranslatedPane(dimensions, chart_index, (double) minX, (double) maxX, (double) minY,
		(double) maxY, label);
	}

	private void initialize(Environment env, Chart plot, Stage stage)
	{
		try
		{
			panels = new ArrayList<JPanel>();
			plots = new ArrayList<SubChartView>();
			this.env = env;
			this.plot = plot;
			mainPane = new BorderPane();
			plotPane = new Pane();
			mainPane.setPrefSize(plot.getWidth(), plot.getHeight());
			plotPane.setPrefSize(plot.getWidth(), plot.getHeight());
			addOnPane = new BorderPane();
			addOnPane.setCenter(plotPane);
			// addOnPane.setBackground(null);
			if (plot.getMainTitle() != null)
			{
				createSwingContent();
			}
			generatePlots();

			setupSave();
			// setupMenus();
			ScrollPane scroller = new ScrollPane();
			scroller.setVbarPolicy(ScrollBarPolicy.NEVER);
			// scroller.setBackground(null);
			editorPane = new BorderPane();
			scroller.setContent(addOnPane);
			editorPane.setCenter(scroller);
			VBox b = new VBox(editorPane);
			b.setFillWidth(false);
			b.setAlignment(Pos.CENTER);
			mainPane.setCenter(b);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void setupStage(Stage stage, boolean stay_open)
	{
		if (stage != null)
		{
			if (stay_open)
			{
				// stage
				// .setScene(new Scene(mainPane, plot.getWidth() + 5.0, plot.getHeight() + plot.measureFont() + 50.0));
				stage.setScene(new Scene(mainPane, plot.getWidth() + 2.0, plot.getHeight() + plot.measureFont() + 2.0));
				stage.show();
				stage.setResizable(false);
			} else
			{
				stage.initStyle(StageStyle.TRANSPARENT);
				stage.setAlwaysOnTop(false);
				stage.setScene(new Scene(mainPane, 1, 1));
				stage.show();
			}
			// stage.showAndWait();
		}
	}

	private Graphics2D paintGraphics(Graphics2D graphics)
	{
		for (SubChartView pane : plots)
		{

			double widthPercent = (pane.getPane().widthProperty().get());
			double heightPercent = (pane.getPane().heightProperty().get());
			double xPos = pane.getPane().getLocalToSceneTransform().getTx()
			- this.plotPane.getLocalToSceneTransform().getTx();
			double yPos = plot.measureFont() + pane.getPane().getLocalToSceneTransform().getTy()
			- this.plotPane.getLocalToSceneTransform().getTy();
			Rectangle r = new Rectangle((int) (xPos), (int) (yPos), (int) (widthPercent), (int) (heightPercent));
			pane.getChart().draw(graphics, r);

		}
		for (JPanel panel : panels)
		{
			panel.printAll(graphics);
		}
		return graphics;
	}

	private void prepareBackgroundsForGraphicsCapture(boolean load_background)
	{
		for (SubChartView pane : plots)
		{
			Paint background = null;
			if (load_background)
			{
				background = Color.white;
			}
			try
			{
				pane.getChart().getLegend().setBackgroundPaint(background);
				pane.getChart().getPlot().setBackgroundPaint(background);
				pane.getChart().setBackgroundPaint(background);
			} catch (Exception e)
			{

				e.printStackTrace();
			}
		}
		for (Integer p = 0; p < panels.size(); p++)
		{
			JPanel panel = panels.get(p);
			// BorderPane pane = panez.get(p - 1);
			// pane.setBackground(null);
			panel.setOpaque(load_background);
			// if (load_background)
			{
				// panel.setBackground(Color.white);
			}
		}
	}

	private Graphics2D prepareVectorGraphics(boolean image, BufferedImage bi)
	{
		Graphics2D graphics = null;
		if (image)
		{
			graphics = bi.createGraphics();
		} else
		{
			graphics = new VectorGraphics2D();
		}
		return graphics;
	}

	public void setupSave()
	{
		mainPane.setOnKeyTyped(new EventHandler<KeyEvent>()
		{

			public void handle(KeyEvent event)
			{
				String keys = event.getCharacter();
				if ((event.isShortcutDown() || event.isControlDown()) && keys.equals("s"))
				{
					FileChooser choose = new FileChooser();
					choose.setInitialFileName("Specify Chart Save Destination");
					HashMap<ExtensionFilter, ImageFormat> formats = new HashMap<ExtensionFilter, ImageFormat>();
					ImageFormat[] formatz = new ImageFormat[]
					{ ImageFormat.BMP, ImageFormat.EPS, ImageFormat.GIF, ImageFormat.JPEG, ImageFormat.PNG,
							ImageFormat.SVG };
					for (ImageFormat formatt : formatz)
					{
						ExtensionFilter fil = new ExtensionFilter(formatt.name.toUpperCase(), "*" + formatt.extension);
						choose.getExtensionFilters().add(fil);
						formats.put(fil, formatt);
					}

					choose.setInitialFileName("chart");
					File f = choose.showSaveDialog(new Stage());
					// File fe = new File(f.getAbsolutePath().replaceAll(".", ""));
					captureGraphic(f, formats.get(choose.getSelectedExtensionFilter()));
				}
			}
		});
	}

	public ChartView(Environment env, Chart plot, Stage stage)
	{

		initialize(env, plot, stage);
		setupStage(stage, true);
	}

	public ChartView(Environment env, Chart plot, Stage stage, FileSpecifications<ImageFormat> output)
	{
		closeablePlots++;
		Console.info("Chart saved: " + output.getLocation(false) + output.getFormat().extension);
		initialize(env, plot, stage);
		setupStage(stage, false);
		getOutput(output, stage);
	}

	public static boolean closeablePlotsOpen()
	{
		return closeablePlots > 0;
	}

	// private void createLabel(BorderPane label_pane, FreeLabel label, Integer ind)
	// {
	// final SwingNode swingNode = new SwingNode();
	//
	// SwingUtilities.invokeLater(new Runnable()
	// {
	//
	// @Override
	// public void run()
	// {
	// // SwingUtilities.invokeLater(new Runnable()
	// // {
	// //
	// // @Override
	// // public void run()
	// // {
	//
	// JLabel jlabel = new JLabel(" " + label.getText() + " ");
	// Font font = label.getFont();
	// jlabel.setFont(font);
	//
	// JPanel panel = new JPanel();
	// panel.add(jlabel, BorderLayout.CENTER);
	// // panel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
	// panel.setAlignmentX((float) label_pane.translateXProperty().get());
	// panel.setAlignmentY((float) label_pane.translateYProperty().get());
	// // panel.repaint(
	// // new Rectangle((int) label_pane.translateXProperty().get(), (int)
	// // label_pane.translateYProperty().get(),
	// // (int) label_pane.getPrefWidth(), (int) label_pane.getPrefHeight()));
	// // System.out.println(panel);
	// swingNode.setContent(panel);// panel);
	// panels.add(panel);
	// }
	// });
	// // }
	// // });
	// label_pane.setCenter(swingNode);
	// }
	//
	// private BorderPane createSizedAndTranslatedPane(Integer[][] dimensions, Integer chart_index, Double x_min,
	// Double x_max, Double y_min, Double y_max, boolean label)
	// {
	//
	// Double widthGrid = plot.getWidth() / dimensions[0].length;
	// Double heightGrid = (plot.getHeight(true) - plot.getHeightOffset(chart_index)) / dimensions.length;
	// Double x = x_min * widthGrid;
	// Double y = (y_min * heightGrid);
	// if (label)
	// {
	// if (plot.getExtraLabels().get(chart_index).getPosition().equals(LabelPosition.BELOW))
	// {
	// y = ((y_min + 1) * heightGrid);
	// }
	// }
	// Double w = (1 + (x_max - x_min)) * widthGrid;
	// Double h = (1 + (y_max - y_min)) * heightGrid;// - plot.measureFont();
	// if (label)
	// {
	// h = plot.measureFont(plot.getExtraLabels().get(chart_index).getFont());
	// }
	// BorderPane pan = new BorderPane();
	// labelOffsets.put(chart_index, y);
	// System.out.println(y);
	// pan.setMaxSize(w, h);
	// pan.setMinSize(w, h);
	// pan.setPrefSize(w, h);
	// pan.setTranslateX(x);
	// pan.translateYProperty().set(y);
	//
	// return pan;
	// }

	// public void generateLabels()
	// {
	// SubChart pf = null;
	// HashMap<Integer, Integer[][]> dimensions = plot.getLabelLocations();
	// for (Integer paneIndex : dimensions.keySet())
	// {
	// BorderPane p = createSubChartPane(dimensions.get(paneIndex), paneIndex, true);
	// createLabel(p, plot.getExtraLabels().get(paneIndex), paneIndex);
	//
	// panez.add(p);
	// // plots.add(p);
	// plotPane.getChildren().add(p);
	// // plotPane.setMaxSize(plot.getWidth(), plot.getHeight());
	// }
	//
	// }

	// private Graphics2D paintGraphics(Graphics2D graphics)
	// {
	// for (SubChart pane : plots)
	// {
	//
	// double widthPercent = (pane.getPane().widthProperty().get());
	// double heightPercent = (pane.getPane().heightProperty().get());
	// double xPos = pane.getPane().getLocalToSceneTransform().getTx()
	// - this.plotPane.getLocalToSceneTransform().getTx();
	// double yPos = plot.measureFont() + pane.getPane().getLocalToSceneTransform().getTy()
	// - this.plotPane.getLocalToSceneTransform().getTy();
	// Rectangle r = new Rectangle((int) (xPos), (int) (yPos), (int) (widthPercent), (int) (heightPercent));
	// pane.getChart().draw(graphics, r);
	//
	// }
	// for (JPanel panel : panels)
	// {
	// System.out.println(panel);
	// // graphics.create(0, 734, 500, 27);
	// if (!panel.equals(panels.get(0)))
	// {
	// Integer paneIndex = panels.indexOf(panel) - 1;
	// BorderPane p = panez.get(paneIndex);
	// panel.printAll(graphics.create((int) p.getTranslateX(), (int) (p.getTranslateY() + plot.measureFont()),
	// (int) p.getWidth(), (int) p.getHeight()));
	// } else
	// {
	// panel.printAll(graphics);
	// }
	// // panel.printAll(graphics);// .getComponent(0).repaint(0, 0, 734, 500, 27);
	// // panel.getComponent(0)..prin.paint(graphics);
	// }
	// return graphics;
	// }
}
