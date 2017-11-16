package edu.cross.ucsc.hse.core.chart;

import com.be3short.io.format.FileSpecifications;
import com.be3short.io.format.ImageFormat;
import com.be3short.obj.modification.ObjectCloner;
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Size2D;

public class ChartView
{

	private BorderPane addOnPane;
	private BorderPane editorPane;
	private EnvironmentData env;
	private BorderPane mainPane;
	private FileSpecifications<ImageFormat> output;
	private ArrayList<JPanel> panels;
	private ChartConfiguration plot;
	private Pane plotPane;
	private ArrayList<SubChartView> plots;
	private LegendTitle globalLegend;

	public void exportToFile(File f, ImageFormat format)
	{

		SwingUtilities.invokeLater(new Runnable()
		{

			@Override
			public void run()
			{

				double width = plotPane.widthProperty().get();
				double height = plotPane.heightProperty().get() + measureFont() + drawGlobalLegend().height;
				BufferedImage bi = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
				prepareBackgroundsForGraphicsCapture(format.needsBackground);// format.needsBackground);
				Graphics2D graphics = prepareVectorGraphics(format.image, bi);
				graphics = paintGraphics(graphics);
				createGraphicFile(f, format, graphics, bi);

			}
		});
	}

	public ChartConfiguration getChartConfiguration()
	{
		return plot;
	}

	public EnvironmentData getEnvironmentData()
	{
		return env;
	}

	public BorderPane getMainPane()
	{
		return mainPane;
	}

	public boolean renderContents()
	{
		CombinedDomainXYPlot mplot = null;
		if (plot.isCombinedDomainPlot())
		{
			mplot = (CombinedDomainXYPlot) ObjectCloner.xmlClone(plot.getCombinedPlot());
		}
		try
		{
			env.getAllStoredObjectSetNames();
			plots.clear();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		SubChartView pf = null;
		HashMap<Integer, Integer[][]> dimensions = ChartConfiguration.ChartOperations.getChartLocations(plot);
		for (Integer paneIndex : dimensions.keySet())
		{
			pf = null;
			if (env == null)
			{
				pf = new SubChartView(createSubChartPane(dimensions.get(paneIndex), paneIndex, false), plot, paneIndex,
				this);
			} else
			{
				if (plot.chartProperties(paneIndex).getyDataSelection() != null)
				{
					pf = new SubChartView(env, createSubChartPane(dimensions.get(paneIndex), paneIndex, false),
					plot.chartProperties(paneIndex).getyDataSelection(), plot, paneIndex, this);
				} else
				{
					pf = new SubChartView(env, createSubChartPane(dimensions.get(paneIndex), paneIndex, false), plot,
					paneIndex, this);
				}
			}
			if (plot.isCombinedDomainPlot())
			{
				Double weight = (pf.getPane().getPrefHeight() * 100) / plot.getHeight();
				mplot.add(pf.getChart().getXYPlot(), weight.intValue());
			} else
			{
				plots.add(pf);
				plotPane.getChildren().add(pf.getPane());
				plotPane.setMaxSize(plot.getWidth(), plot.getHeight());
			}

		}
		if (plot.isCombinedDomainPlot())
		{

			BorderPane pane = new BorderPane();
			pane.setPrefSize(plot.getWidth(), plot.getHeight());
			pf = new SubChartView(new JFreeChart("HSE Plot", JFreeChart.DEFAULT_TITLE_FONT, mplot, true), pane,
			this.plot, this);
			plots.add(pf);
			plotPane.getChildren().add(pf.getPane());
		}

		cleanMultiDomainLegend(mplot);
		plotPane.setMaxSize(plot.getWidth(), plot.getHeight());
		return true;
	}

	private void cleanMultiDomainLegend(CombinedDomainXYPlot mplot)
	{
		if (plot.isCombinedDomainPlot())
		{
			ArrayList<String> existingLabels = new ArrayList<String>();
			LegendItemCollection items = new LegendItemCollection();
			System.out.println(mplot.getLegendItems().getItemCount());
			for (int i = 0; i < mplot.getLegendItems().getItemCount(); i++)
			{

				LegendItem item = mplot.getLegendItems().get(i);
				System.out.println(item.getLabel());
				if (!existingLabels.contains(item.getLabel()))
				{
					items.add(item);
					existingLabels.add(item.getLabel());
				}
			}
			mplot.setFixedLegendItems(items);
		}
	}

	// private boolean renderContents()// CustomLayout()
	// {
	// try
	// {
	// env.getAllStoredObjectSetNames();
	// plots.clear();
	// } catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	// SubChartView pf = null;
	// HashMap<Integer, Integer[][]> dimensions = ChartConfiguration.ChartOperations.getChartLocations(plot);
	// for (Integer paneIndex : dimensions.keySet())
	// {
	// pf = null;
	// if (env == null)
	// {
	// pf = new SubChartView(createSubChartPane(dimensions.get(paneIndex), paneIndex, false), plot, paneIndex,
	// this);
	// } else
	// {
	// if (plot.chartProperties(paneIndex).getyDataSelection() != null)
	// {
	// pf = new SubChartView(env, createSubChartPane(dimensions.get(paneIndex), paneIndex, false),
	// plot.chartProperties(paneIndex).getyDataSelection(), plot, paneIndex, this);
	// } else
	// {
	// pf = new SubChartView(env, createSubChartPane(dimensions.get(paneIndex), paneIndex, false), plot,
	// paneIndex, this);
	// }
	// }
	// plots.add(pf);
	// plotPane.getChildren().add(pf.getPane());
	//
	// }
	// plotPane.setMaxSize(plot.getWidth(), plot.getHeight());
	// return true;
	// }

	public void setChartConfiguration(ChartConfiguration plot)
	{
		this.plot = plot;
		renderContents();
	}

	public void setEnvironment(EnvironmentData env)
	{
		this.env = env;
		renderContents();
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
		Double heightGrid = ((getHeight(true)) - drawGlobalLegend().height) / dimensions.length;
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
				Font font = plot.getMainTitleFont();
				label.setFont(font);

				JPanel panel = new JPanel();
				panel.add(label, BorderLayout.CENTER);

				// , new Rectangle(100,
				// 100));
				// panel.setAlignmentX(JPanel.CENTER_ALIGNMENT);

				swingNode.setContent(panel);// panel);
				panels.add(panel);

				// SwingUtilities.invokeLater(new Runnable()
				// {
				//
				// @Override
				// public void run()
				// {
				//
				// LegendTitle title = (LegendTitle) ObjectCloner.xmlClone(plots.get(0).getChart().getLegend());
				// title.setVisible(true);
				// System.out.println(XMLParser.serializeObject(title));
				//
				// // title.arrange((Graphics2D) panel.getGraphics());
				// title.draw((Graphics2D) panel.getGraphics(), panel.getBounds());//
				// chart.getLegend().draw((Graphics2D)
				// // g,
				// // new
				//
				// }
				// });
				// Rectangle(0,
				// 0,
				// this.getWidth(),
				// this.getHeight()));//plots.get(0).getChart().getLegend().arrange((Graphics2D)
				// panel.getGraphics());
			}
		});
		addOnPane.setTop(swingNode);
	}

	private Double getHeight(boolean adjusted)
	{
		Double heightAdj = plot.getHeight();
		if (plot.getMainTitle() != null)
		{
			if (plot.getMainTitle().length() > 0)
			{
				heightAdj = plot.getHeight() - LabelProperties.measureFont(plot.getMainTitleFont());
			}
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

							exportToFile(out, output.getFormat());// .getMatch(action.label));

							success.setValue(true);

						} catch (Exception e)
						{
							e.printStackTrace();
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

	private void initialize(EnvironmentData env, ChartConfiguration plot, Stage stage)
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

			renderContents();
			if (plot.getMainTitle() != null)
			{
				createSwingContent();
			}

			final SwingNode n = new SwingNode();
			// createSwingLabel(n);
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

			final SwingNode swingNode = new SwingNode();
			if (this.getChartConfiguration().isDisplayGlobalLegend())
			{
				createSwingLabel(swingNode);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private Size2D drawGlobalLegend()
	{
		Size2D size = new Size2D(0.0, 0.0);
		try
		{
			BufferedImage bi = new BufferedImage(getChartConfiguration().getWidth().intValue(),
			getChartConfiguration().getHeight().intValue(), BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = prepareVectorGraphics(false, bi);
			size = globalLegend.arrange(graphics);
			globalLegend.draw(graphics,
			new Rectangle((int) ((this.getChartConfiguration().getWidth() - size.width) / 2),
			this.getChartConfiguration().getHeight().intValue(), this.getChartConfiguration().getWidth().intValue(),
			this.getChartConfiguration().getHeight().intValue()));
		} catch (Exception e)
		{

		}
		return size;
	}

	// private void initializeGlobalLegend()
	// {
	// ArrayList<LegendItem> legendItems = new ArrayList<LegendItem>();
	// for (SubChartView ch : plots)
	// {
	//
	// try
	// {
	// for (int i = 0; i < ch.getChart().getXYPlot().getLegendItems().getItemCount(); i++)
	// {
	// LegendItem item = ch.getChart().getXYPlot().getLegendItems().get(i);
	// if (!legendItems.contains(item))
	// {
	// legendItems.add(item);
	// }
	// }
	// } catch (Exception e)
	// {
	//
	// }
	// }
	//
	// JFreeChart ch = (JFreeChart) ObjectCloner.xmlClone(getChartConfiguration().chartTemplate);
	// ch.getLegend().setVisible(true);
	// for (LegendItem item : legendItems)
	// {
	// ch.getPlot().getLegendItems().add(item);
	// }
	//
	// globalLegend = ch.getLegend();// ObjectCloner.xmlClone(plots.get(0).getChart().getPlot()));
	// }

	private Double measureFont()
	{
		Double height = 0.0;
		if (plot.getMainTitle() != null)
		{
			height = LabelProperties.measureFont(plot.getMainTitleFont());
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
		try
		{
			Size2D size = globalLegend.arrange(graphics);
			globalLegend.draw(graphics,
			new Rectangle((int) ((this.getChartConfiguration().getWidth() - size.width) / 2),
			this.getChartConfiguration().getHeight().intValue(), this.getChartConfiguration().getWidth().intValue(),
			this.getChartConfiguration().getHeight().intValue()));
			// System.out.println(XMLParser.serializeObject(globalLegend.arrange(graphics)));
		} catch (Exception e)
		{

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
				panel.setBackground(Color.white);
			}
		}
		try
		{
			if (load_background)
			{
				globalLegend.setBackgroundPaint(Color.white);
			} else
			{
				globalLegend.setBackgroundPaint(null);
			}
		} catch (Exception e)
		{
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
					exportToFile(f, formats.get(choose.getSelectedExtensionFilter()));
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

	public ChartView(EnvironmentData env, ChartConfiguration plot, Stage stage)
	{

		initialize(env, plot, stage);
		setupStage(stage, true);
	}

	public ChartView(EnvironmentData env, ChartConfiguration plot, Stage stage, FileSpecifications<ImageFormat> output)
	{
		Console.info("Chart saved: " + output.getLocation(false) + output.getFormat().extension);
		initialize(env, plot, stage);
		setupStage(stage, false);
		getOutput(output, stage);
	}

	// public static class MyPanel extends JPanel
	public class MyPanel extends JPanel
	{

		SubChartView v;
		private int squareX = 0;
		private int squareY = 0;
		private int squareW = 20;
		private int squareH = 20;
		JFreeChart chart;
		ChartView panez;

		public MyPanel(ChartView panez, EnvironmentData chartz, ChartView p)
		{
			v = new SubChartView(chartz, new BorderPane(), panez.plot, 0, panez);
			p.getChartConfiguration().chartProperties(0).setDisplayLegend(true);
			v.toggleLegendVisibility(true);// .getChart().getLegend().setVisible(true);
			this.panez = panez;
			// v.getProps().addToYFilter(v.getFieldNames().get(0));
			SwingUtilities.invokeLater(new Runnable()

			{

				@Override
				public void run()
				{
					chart = v.getChart();

					ArrayList<LegendItem> legendItems = new ArrayList<LegendItem>();
					XYSeriesCollection ser = (XYSeriesCollection) chart.getXYPlot().getDataset();
					for (SubChartView ch : plots)
					{
						XYSeriesCollection series = (XYSeriesCollection) ch.getChart().getXYPlot().getDataset();
						for (int in = 0; in < series.getSeriesCount(); in++)
						{
							XYSeries serie = series.getSeries(in);
							try
							{
								ser.addSeries(serie);
							} catch (Exception e)
							{

							}
						}
						try

						{
							for (int i = 0; i < ch.getChart().getXYPlot().getLegendItems().getItemCount(); i++)
							{
								LegendItem item = ch.getChart().getXYPlot().getLegendItems().get(i);
								if (!legendItems.contains(item))
								{
									legendItems.add(item);
								}
							}
						} catch (Exception e)
						{
							e.printStackTrace();
						}
					}

					// v.getProps().setLegendVisible(true);
					// this.chart = v.createChart();// (new ViewPane(chart, p, null)).getChart().getChart();

					for (LegendItem item : legendItems)
					{
						chart.getPlot().getLegendItems().add(item);
					}

					chart.getLegend().setVisible(true);
					chart.getLegend().setBackgroundPaint(null);
					globalLegend = chart.getLegend();
					drawGlobalLegend();

				}
			});

			// this.chart = v.getChart();
			//
			// ArrayList<LegendItem> legendItems = new ArrayList<LegendItem>();
			// XYSeriesCollection ser = (XYSeriesCollection) this.chart.getXYPlot().getDataset();
			// for (SubChartView ch : plots)
			// {
			// XYSeriesCollection series = (XYSeriesCollection) ch.getChart().getXYPlot().getDataset();
			// for (int in = 0; in < series.getSeriesCount(); in++)
			// {
			// XYSeries serie = series.getSeries(in);
			// ser.addSeries(serie);
			// }
			// try
			//
			// {
			// for (int i = 0; i < ch.getChart().getXYPlot().getLegendItems().getItemCount(); i++)
			// {
			// LegendItem item = ch.getChart().getXYPlot().getLegendItems().get(i);
			// if (!legendItems.contains(item))
			// {
			// legendItems.add(item);
			// }
			// }
			// } catch (Exception e)
			// {
			// e.printStackTrace();
			// }
			// }
			//
			// // v.getProps().setLegendVisible(true);
			// // this.chart = v.createChart();// (new ViewPane(chart, p, null)).getChart().getChart();
			//
			// for (LegendItem item : legendItems)
			// {
			// this.chart.getPlot().getLegendItems().add(item);
			// }

			// globalLegend = ch.getLegend();// ObjectCloner.xmlClone(plots.get(0).getChart().getPlot()));
			this.setLayout(new FlowLayout());
			this.setAlignmentX((float) 0.5);
			// , BorderLayout.CENTER);
			setBorder(BorderFactory.createLineBorder(Color.black));
			// setBackground(null);
			// setOpaque(false);

			addMouseListener(new MouseAdapter()
			{

				public void mousePressed(MouseEvent e)
				{
					moveSquare(e.getX(), e.getY());
				}
			});

			addMouseMotionListener(new MouseAdapter()
			{

				public void mouseDragged(MouseEvent e)
				{
					moveSquare(e.getX(), e.getY());
				}
			});

		}

		private void moveSquare(int x, int y)
		{
			update();

			int OFFSET = 1;
			// if ((squareX != x) || (squareY != y))
			{
				repaint(squareX, squareY, this.getWidth(), this.getHeight());
				squareX = 0;
				squareY = 0;
				repaint(squareX, squareY, this.getWidth(), this.getHeight());
			}
		}

		public void update()
		{
			// v.getProps().getyFilters().clear();
			// v.getProps().setLegendVisible(true);
			// for (ViewPane fn : panez.childPanes)
			{

				// v.getProps().addToYFilter(fn.getChart().getProps().getyFilters().get(0));
			}

			// v.getProps().addToYFilter(v.getFieldNames().get(0));
			// l.draw((Graphics2D) this.getGraphics(), new Rectangle(this.getWidth() / 2, this.getHeight()));
			// v.getProps().setLegendVisible(true);
			// this.chart = v.createChart();
		}

		public Dimension getPreferredSize()
		{
			return new Dimension(25, 20);
		}

		protected void paintComponent(Graphics g)
		{
			this.chart.getLegend().setBackgroundPaint(null);
			super.paintComponent(g);
			drawGlobalLegend();
			// this.chart.getLegend().arrange((Graphics2D) g, new RectangleConstraint(this.getWidth(),
			// this.getHeight()));// ,
			// new
			// RectangleConstraint(this.getWidth(),
			// this.getHeight()));
			// this.chart.getLegend().draw((Graphics2D) g, new Rectangle(this.getWidth(), this.getHeight()));
			// g.drawRect(squareX, squareY, squareW, squareH);
		}

	}

	private void createSwingLabel(final SwingNode swingNode)
	{
		final EnvironmentData data = env;
		final ChartView mp = this;
		ChartView panez = this;

		MyPanel p = new MyPanel(panez, data, mp);

		// JFreeChart c = createCombinedChart();
		// p.setChart(c);
		// JFrame f = new JFrame();
		// JPanel pan = createDemoPanel();
		// table.add((Component) l.getItemContainer().getFrame().);
		// f.add(p);
		// p.add(table, BorderLayout.CENTER);
		// panels.add(p);
		// p.repaint();
		// p.setOpaque(true);
		// JPanel pan = new JPanel();
		// pan.add(p);
		// f.add(p);
		// Graphics2D g =
		// JFreeChart c = createCombinedChart();
		// p.setChart(c);
		// p.setPreferredSize(new Dimension(200, 200));
		swingNode.setContent(p);

		BorderPane lp = new BorderPane();
		lp.setCenter(swingNode);
		addOnPane.setBottom(lp);

	}

}
