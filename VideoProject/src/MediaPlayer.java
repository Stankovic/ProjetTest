import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.media.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;

/*****************************
 * MediaPlayer Class *
 *****************************/
class MediaPlayer extends JFrame implements ControllerListener {
	private Player player = null;
	private JPanel videoPanel = null;
	private JSlider slider = null;
	private volatile Thread slider_updater = null;
	private volatile boolean follows_slider = true;
	private JToggleButton start_stop = new JToggleButton();
	float step = 1.0f / 10;

	private JMenuBar menu_bar = null; // menu bar used for the different Buttons
	private JFrame frame = null; // frame used to open a file
	private JFileChooser fc = null; // used for the dialog window to open a file
	private File file;

	/* Various Buttons */
	private JButton open = null;
	private JButton play = null;
	private JButton pause = null;
	private JButton stop = null;
	private JButton about = null;
	private CLSr csr = null;
	private Time duration = null;

	/***********************************************
	 * MediaPlayer Builder * needs the address of a movie as an argument *
	 ***********************************************/
	public MediaPlayer(String nomFilm) {
		super(); /*
				 * Constructs a new frame that is initially invisible. This
				 * constructor sets the component's locale property to the value
				 * returned by JComponent.
				 */
		setLocation(400, 200); /*
								 * Moves this component to a new location. The
								 * top-left corner of the new location is
								 * specified by the x and y parameters in the
								 * coordinate space of this component's parent.
								 */
		setTitle("Video Player"); // Sets the title for this frame to the
									// specified string.
		getContentPane().setLayout(new BorderLayout()); /*
														 * Sets the layout
														 * manager for this
														 * container. Constructs
														 * a new border layout
														 * with no gaps between
														 * components.
														 */
		addWindowListener(new WindowAdapter() /*
											 * Adds the specified window
											 * listener to receive window events
											 * from this window
											 */
		{
			public void windowClosing(WindowEvent we) /*
													 * Invoked when a window is
													 * in the process of being
													 * closed. The close
													 * operation can be
													 * overridden at this point.
													 */
			{
				JOptionPane.showMessageDialog(null,
						"Thank you to have used Video Player", "Quit",
						JOptionPane.INFORMATION_MESSAGE);
				/*
				 * Brings up a dialog that displays a message using a default
				 * icon determined by the messageType parameter.
				 */
				System.exit(0); // Terminates the currently running Java Virtual
								// Machine.
			}
		});

		if (nomFilm != null)
			loadMovie(nomFilm); // load the movie
	}

	/******************************************
	 * method of loading of film from its URL *
	 ******************************************/
	private void loadMovie(String movieURL) {
		if (movieURL.indexOf(":") < 3)
			movieURL = "file:" + movieURL;
		try { // creation of the player

			player = Manager.createPlayer(new MediaLocator(movieURL));
			player.addControllerListener(this);
			player.realize();
		} catch (Exception e) {
			System.out.println("Error creating player");
			return;
		}
	}

	private void validateSlider() {
		Time tm = duration;
		System.out.println("toto" + tm.getSeconds());
		slider.setMaximum(1160);
		slider.setMinimum(0);
		// creates a slider with the specified orientation and the specified
		// minimum, maximum, and initial values

	}

	/********************************************************
	 * intercept all the events in provenence of the player *
	 ********************************************************/
	public void controllerUpdate(ControllerEvent ce) {
		// to change the icon of tje player
		Toolkit tk = Toolkit.getDefaultToolkit();
		setIconImage(new ImageIcon("videoPlayer/icons/icon.gif").getImage());

		// to give the duration of the movie
		if (ce instanceof DurationUpdateEvent) {
			duration = ((DurationUpdateEvent) ce).getDuration();
			System.out.println("duration: " + (int) duration.getSeconds()
					+ " seconds");
		}

		// to start the video and create all the buttons etc...
		if (ce instanceof RealizeCompleteEvent) {
			if (menu_bar == null) {
				// creation of the menu bar
				menu_bar = new JMenuBar();

				// creation of the different buttons with the icons
				open = new JButton(new ImageIcon("videoPlayer/icons/open.gif"));
				open.setMargin(new Insets(0, 0, 0, 0));
				play = new JButton(new ImageIcon("videoPlayer/icons/play.gif"));
				play.setMargin(new Insets(0, 0, 0, 0));
				pause = new JButton(
						new ImageIcon("videoPlayer/icons/pause.gif"));
				pause.setMargin(new Insets(0, 0, 0, 0));
				stop = new JButton(new ImageIcon("videoPlayer/icons/stop.gif"));
				stop.setMargin(new Insets(0, 0, 0, 0));
				about = new JButton(
						new ImageIcon("videoPlayer/icons/about.gif"));
				about.setMargin(new Insets(0, 0, 0, 0));

				// creation of the frame used to open a file
				frame = new JFrame();
				fc = new JFileChooser();

				// the buttons are add to the menu bar
				menu_bar.add(open);
				menu_bar.add(play);
				menu_bar.add(pause);
				menu_bar.add(stop);
				menu_bar.add(about);
				System.out.println("validate");
				Time tm = player.getDuration();
				System.out.println(tm.getSeconds());
				slider = new JSlider(JSlider.HORIZONTAL, 0,
						(int) (tm.getSeconds()), 0);
				csr = new CLSr();
				slider.addChangeListener(csr);

				slider.setMajorTickSpacing(10); // This method sets the major
												// tick spacing
				slider.setMinorTickSpacing(2); // This method sets the minor
												// tick spacing
				slider.setPaintTicks(true); // Determines whether tick marks are
											// painted on the slider
				slider.setPaintLabels(false); // Determines whether labels are
												// painted on the slider
				slider.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Sets
																				// the
																				// border
																				// of
																				// this
																				// component
				menu_bar.add(slider); // Appends the slider to the end of the
										// menu bar

				// to set the menu bar
				setJMenuBar(menu_bar);

				// actions which are made while pressing the open button
				open.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						slider.removeChangeListener(csr);
						// modification of the icon
						Toolkit tk = Toolkit.getDefaultToolkit();
						frame.setIconImage(new ImageIcon(
								"videoPlayer/icons/icon.gif").getImage());

						System.out.println("Open a file");

						// posting of a window "of opening"
						int choix = fc.showOpenDialog(frame);

						if (choix == JFileChooser.APPROVE_OPTION) {
							file = fc.getSelectedFile(); // recover the selected
															// file
							String address = file.getPath();// recover the
															// address of the
															// file
							loadMovie(address);
							player.start();
							validateSlider();
							slider.addChangeListener(csr);
						}
					}

				});

				// actions which are made while pressing the play button
				play.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						player.start();
						// player.setMediaTime(new Time(0.0));
						while (play.isSelected()) {
							Time tm = player.getMediaTime();
							double t = tm.getSeconds();
							if (t > 0.0) {
								player.setMediaTime(new Time(t - step));
							}
						}
						System.out.println("Playing movie");
					}
				});

				// actions which are made while pressing the pause button
				pause.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						player.stop();
						player.deallocate();
						System.out.println("Pause");
					}
				});

				// actions which are made while pressing the stop button
				stop.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						player.stop();
						player.deallocate();
						System.out.println("Stop");
						player.setMediaTime(new Time(0)); // puts the video at
															// the beginning
						if (player.getTargetState() < Player.Started)
							player.prefetch();
					}
				});

				// actions which are made while pressing the about button
				about.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.out.println("About");
						// Brings up a dialog that displays a message using a
						// default icon determined by the messageType parameter
						JOptionPane
								.showMessageDialog(
										null,
										" Video Player 1.1\n Video Player Java using JMF\n\n version 1.1",
										"About Video Player",
										JOptionPane.INFORMATION_MESSAGE);
					}
				});
			}

			if (videoPanel == null) { // creation of the panel of sight
				videoPanel = new JPanel();
				videoPanel.setLayout(new BorderLayout());
				getContentPane().add(videoPanel, BorderLayout.CENTER);
			} else
				videoPanel.removeAll();

			// obtaining the component restoring the image in provenence of the
			// player.
			Component vis = player.getVisualComponent();
			if (vis != null) { // if it is valid then we put it in our sight
				videoPanel.add(vis, BorderLayout.CENTER);
				videoPanel.setVisible(true);
				this.pack(); // resize the size according to the size of film
			}
		}

		else if (ce instanceof EndOfMediaEvent) {
			if (player != null) { // stop the movie
				player.stop();
				player.deallocate(); /*
									 * Deallocating the Player releases any
									 * resources that would prevent another
									 * Player from being started. For example,
									 * if the Player uses a hardware device to
									 * present its media, deallocate frees that
									 * device so that other Players can use it.
									 */
			}
		}
	}

	/************
	 * Main *
	 ************/
	public static void main(String[] args) { // needs the address of a movie:
												// *.avi,*.mpg...
		new MediaPlayer("open.avi").setVisible(true);
	}

	public class CLSr implements javax.swing.event.ChangeListener {
		public CLSr() {
		}

		public void stateChanged(ChangeEvent e) {
			System.out.println("state");
			float tm = slider.getValue();
			System.out.println("value=" + tm);
			player.setMediaTime(new Time(tm));
		}
	}
}