package org.osgp.client.gui;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.osgp.client.GetStatistics;
import org.osgp.client.dao.ClientDbsMgr;
import org.osgp.client.setup.InsertDevOpsBundleAndSendHelper;
import org.osgp.util.SystemPropertyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShowStatisticsAwt {

	private static final Logger LOGGER = LoggerFactory.getLogger(ShowStatisticsAwt.class.getName());
	private GetStatistics statisticsClient = null;
	private JTextArea txt = null;

	ShowStatisticsAwt() {
		try {
			ClientDbsMgr.INSTANCE.open();
			statisticsClient = new GetStatistics();
			setupGui();
			loop();
		} finally {
			ClientDbsMgr.INSTANCE.close();
		}
	}

	private void setupGui() {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent we) {
				ClientDbsMgr.INSTANCE.close();
				System.exit(0);
			}
		});

		JTextField propsField = addSystemPropsLabelAndField(f);
		JTextField totalField = addAskTotalLabelAndField(f);

		JLabel label2 = new JLabel("deviceoperations ");
		label2.setBounds(230, 30, 120, 20);
		f.add(label2);

		JButton btn = new JButton("Start");
		btn.setBounds(350, 30, 70, 20);
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setSystemProperties(propsField.getText());
				txt.setText("start inserting deviceoperations, bundling and send to platform ...");
				String msg = insertAndBundle(Integer.parseInt(totalField.getText()));
				txt.setText(msg);
			}
		});

		f.add(btn);

		addDisplayFrame();

		f.add(txt);
		f.setSize(450, 600);
		f.setLayout(null);
		f.setVisible(true);
	}

	private JTextField addAskTotalLabelAndField(JFrame f) {
		JLabel label1 = new JLabel("Insert & bundle ");
		label1.setBounds(20, 30, 130, 20);
		f.add(label1);

		JTextField fld = new JTextField("1000");
		fld.setBounds(150, 30, 70, 20);
		f.add(fld);
		return fld;
	}

	private JTextField addSystemPropsLabelAndField(JFrame f) {
		JLabel label0 = new JLabel("System props ");
		label0.setBounds(20, 10, 130, 20);
		f.add(label0);

		JTextField fld0 = new JTextField("");
		fld0.setBounds(150, 10, 300, 20);
		f.add(fld0);
		return fld0;
	}

	private void addDisplayFrame() {
		txt = new JTextArea("Quit");
		txt.setBounds(20, 60, 400, 500);
		txt.setBackground(Color.LIGHT_GRAY);
		txt.setMargin(new Insets(5, 5, 5, 5));
	}

	private void setSystemProperties(String str) {
		SystemPropertyHelper.clearProperties();
		SystemPropertyHelper.setProperties(str);
	}

	@SuppressWarnings("unused")
	public static void main(String args[]) {
		ShowStatisticsAwt frame = new ShowStatisticsAwt();
	}

	private void loop() {
		try {
			while (true) {
				String s = statisticsClient.getStatistics();
				txt.setText(s);
				Thread.sleep(5000);
			}
		} catch (Exception t) {
			LOGGER.error("error send rpc " + t);
		} finally {
			try {
				statisticsClient.shutdown();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private String insertAndBundle(Integer total) {
		try {
			statisticsClient.resetStatistics();
			System.out.println(total.toString());
			InsertDevOpsBundleAndSendHelper.main(new String[] { total.toString() });
			return InsertDevOpsBundleAndSendHelper.statistics.toString();
		} catch (Exception e) {
			LOGGER.error("error " + e);
			return "error " + e;
		}

	}

}