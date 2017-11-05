package edu.cross.ucsc.hse.core.chart;

import com.be3short.io.format.FileSpecifications;
import com.be3short.io.format.ImageFormat;
import de.erichseifert.vectorgraphics2d.Document;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import de.erichseifert.vectorgraphics2d.eps.EPSProcessor;
import de.erichseifert.vectorgraphics2d.intermediate.CommandSequence;
import de.erichseifert.vectorgraphics2d.svg.SVGProcessor;
import de.erichseifert.vectorgraphics2d.util.PageSize;
import edu.ucsc.cross.hse.core.container.EnvironmentData;
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

	private BorderPane addOnPane;
	private BorderPane editorPane;
	private EnvironmentData env;
	private BorderPane mainPane;
	private FileSpecifications<ImageFormat> output;
	private ArrayList<JPanel> panels;
	private ChartProperties plot;
	private Pane plotPane;
	private ArrayList<SubChartView> plots;

	public void captureGraphic(File f, ImageFormat format)
	{
		double width = plotPane.widthProperty().get();
		double height = plotPane.heightProperty().get() + measureFont();
		BufferedImage bi = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
		prepareBackgroundsForGraphicsCapture(format.needsBackground);// format.needsBackground);
		Graphics2D graphics = prepareVectorGraphics(format.image, bi);
		graphics = paintGraphics(graphics);
		createGraphicFile(f, format, graphics, bi);

	}

	public ChartProperties getChartProperties()
	{
		return plot;
	}

	public EnvironmentData getEnvironment()
	{
		return env;
	}

	public BorderPane getMainPane()
	{
		return mainPane;
	}

	public void renderContents()
	{
		try
		{
			env.getLabelOrder();
			plots.clear();
		} catch (Exception e)
		{

		}
		SubChartView pf = null;
		HashMap<Integer, Integer[][]> dimensions = ChartProperties.ChartOperations.getChartLocations(plot);
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
					pf = new SubChartView(env, createSubChartPane(dimensions.get(paneIndex), paneIndex, false),
					plot.sub(paneIndex).getyDataSelection(), plot, paneIndex, this);
				} else
				{
					pf = new SubChartView(env, createSubChartPane(dimensions.get(paneIndex), paneIndex, false), plot,
					paneIndex, this);
				}
			}
			plots.add(pf);
			plotPane.getChildren().add(pf.getPane());
			plotPane.setMaxSize(plot.getWidth(), plot.getHeight());
		}

	}

	public void setChartProperties(ChartProperties plot)
	{
		this.plot = plot;
		renderContents();
	}

	public void setEnvironment(EnvironmentData env)
	{
		this.env = env;
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

	private BorderPane createSizedAndTranslatedPane(Integer[][] dimensions, Integer chart_index, Double x_min,
	Double x_max, Double y_min, Double y_max, boolean label)
	{

		Double widthGrid = plot.getWidth() / dimensions[0].length;
		Double heightGrid = (getHeight(true)) / dimensions.length;
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

	private Double getHeight(boolean adjusted)
	{
		Double heightAdj = plot.getHeight();
		if (plot.getMainTitle() != null)
		{
			heightAdj = plot.getHeight()
			- LabelProperties.measureFont(plot.getFonts().get(LabelType.MAIN_TITLE).getFont());
		}
		return heightAdj;
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
				Long attemptTime = System.currentTimeMillis() + (long) 5000;
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
							// e.printStackTrace();
							// Console.error("Unable to create output plot: " + output.toString());
						}
						// your code here

						// }
						// }, (int) 500, (int) 500);
						attemptTime = System.currentTimeMillis() + 5000;
					}
				}
				return 0;
			}

			@Override
			protected void succeeded()
			{
				super.succeeded();
				stage.close();
			}

		};
		new Thread(task).start();

	}

	private void initialize(EnvironmentData env, ChartProperties plot, Stage stage)
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
			setupSave();
			if (plot.getMainTitle() != null)
			{
				createSwingContent();
			}
			renderContents();

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

	private Double measureFont()
	{
		Double height = 0.0;
		if (plot.getMainTitle() != null)
		{
			height = LabelProperties.measureFont(plot.getFonts().get(LabelType.MAIN_TITLE).getFont());
		}
		return height;
	}

	private Graphics2D paintGraphics(Graphics2D graphics)
	{
		for (SubChartView pane : plots)
		{

			double widthPercent = (pane.getPane().widthProperty().get());
			double heightPercent = (pane.getPane().heightProperty().get());
			double xPos = pane.getPane().getLocalToSceneTransform().getTx()
			- this.plotPane.getLocalToSceneTransform().getTx();
			double yPos = pane.measureFont() + pane.getPane().getLocalToSceneTransform().getTy()
			- this.plotPane.getLocalToSceneTransform().getTy();
			Rectangle r = new Rectangle((int) (xPos), (int) (yPos), (int) (widthPercent), (int) (heightPercent));
			pane.getChart().draw(graphics, r);

		}
		for (JPanel panel : panels)
		{
			panel.print(graphics);
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
			}
		}
		for (Integer p = 0; p < panels.size(); p++)
		{
			JPanel panel = panels.get(p);
			panel.setOpaque(load_background);
			if (load_background)
			{
				panel.setBackground(Color.WHITE);
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

	private void setupSave()
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

	private void setupStage(Stage stage, boolean stay_open)
	{
		if (stage != null)
		{
			if (stay_open)
			{
				stage.setScene(new Scene(mainPane, plot.getWidth() + 2.0, plot.getHeight() + measureFont() + 2.0));
				stage.show();
				stage.setResizable(false);
			} else
			{
				stage.initStyle(StageStyle.TRANSPARENT);
				stage.setAlwaysOnTop(false);
				stage.setScene(new Scene(mainPane, 1, 1));
				stage.show();
			}

		}
	}

	public ChartView(EnvironmentData env, ChartProperties plot, Stage stage)
	{

		initialize(env, plot, stage);
		setupStage(stage, true);
	}

	public ChartView(EnvironmentData env, ChartProperties plot, Stage stage, FileSpecifications<ImageFormat> output)
	{
		Console.info("Chart saved: " + output.getLocation(false) + output.getFormat().extension);
		initialize(env, plot, stage);
		setupStage(stage, false);
		getOutput(output, stage);
	}
}
