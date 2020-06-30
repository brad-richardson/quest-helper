/*
 * Copyright (c) 2020, Zoinkwiz
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.questhelper.panel;

import java.util.ArrayList;
import java.util.HashMap;
import com.questhelper.ItemRequirement;
import com.questhelper.steps.QuestStep;
import net.runelite.client.ui.ColorScheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import net.runelite.client.ui.FontManager;
import net.runelite.client.util.SwingUtil;

public class QuestStepPanel extends JPanel
{
	private static final int TITLE_PADDING = 5;

	PanelDetails panelDetails;

    private final JPanel headerPanel = new JPanel();
	private final JLabel headerLabel = new JLabel();
	private final JPanel bodyPanel = new JPanel();
	private final JCheckBox lockStep = new JCheckBox();
	private JPanel leftTitleContainer;
	private JPanel viewControls;

	private QuestStep currentlyHighlighted = null;

	private final HashMap<QuestStep, JLabel> steps = new HashMap<>();

    public QuestStepPanel(PanelDetails panelDetails, QuestStep currentStep)
	{
		this.panelDetails = panelDetails;

		setLayout(new BorderLayout(0, 1));
		setBorder(new EmptyBorder(5, 0, 0, 0));

		leftTitleContainer = new JPanel(new BorderLayout(5, 0));

		headerLabel.setText(panelDetails.getHeader());
		headerLabel.setFont(FontManager.getRunescapeBoldFont());

		headerLabel.setMinimumSize(new Dimension(1, headerLabel.getPreferredSize().height));

		headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
		headerPanel.setBorder(new EmptyBorder(7, 7, 7, 7));

		headerPanel.add(Box.createRigidArea(new Dimension(TITLE_PADDING, 0)));
		leftTitleContainer.add(headerLabel, BorderLayout.CENTER);
		headerPanel.add(leftTitleContainer, BorderLayout.WEST);

		viewControls = new JPanel(new GridLayout(1, 3, 10, 0));

		SwingUtil.addModalTooltip(lockStep, "Mark section as incomplete", "Mark section as complete");
		lockStep.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		lockStep.addActionListener(ev -> lockSection(lockStep.isSelected()));
		lockStep.setVisible(false);
		headerPanel.add(lockStep, BorderLayout.EAST);

		viewControls.add(lockStep);

		headerPanel.add(viewControls, BorderLayout.EAST);

		if (panelDetails.getSteps().contains(currentStep))
		{
			headerLabel.setForeground(Color.BLACK);
			headerPanel.setBackground(ColorScheme.BRAND_ORANGE);
			viewControls.setBackground(ColorScheme.BRAND_ORANGE);
			leftTitleContainer.setBackground(ColorScheme.BRAND_ORANGE);
		}
		else
		{
			headerLabel.setForeground(Color.WHITE);
			headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
			viewControls.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
			leftTitleContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
		}

		/* Body */
		bodyPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
		bodyPanel.setLayout(new BorderLayout());
		bodyPanel.setBorder(new EmptyBorder(10, 5, 10, 5));

		if(!panelDetails.getItemRequirements().isEmpty()) {
			JPanel questItemRequirementsPanel = new JPanel();
			questItemRequirementsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			questItemRequirementsPanel.setLayout(new BorderLayout());
			questItemRequirementsPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

			JPanel questItemRequirementsHeader = new JPanel();
			questItemRequirementsHeader.setBackground(ColorScheme.DARKER_GRAY_COLOR);
			questItemRequirementsHeader.setLayout(new BorderLayout());
			questItemRequirementsHeader.setBorder(new EmptyBorder(5, 5, 5, 10));

			JLabel questItemReqsTitle = new JLabel();
			questItemReqsTitle.setForeground(Color.WHITE);
			questItemReqsTitle.setText("Bring the following items:");
			questItemReqsTitle.setMinimumSize(new Dimension(1, questItemRequirementsHeader.getPreferredSize().height));
			questItemRequirementsHeader.add(questItemReqsTitle, BorderLayout.NORTH);

			JPanel questItemRequirementsListPanel = new JPanel();
			questItemRequirementsListPanel.setLayout(new BorderLayout());
			questItemRequirementsListPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

			JLabel itemLabel = new JLabel();
			itemLabel.setForeground(Color.GRAY);
			StringBuilder itemText = new StringBuilder();
			for (ItemRequirement itemRequirement : panelDetails.getItemRequirements())
			{
				if(!itemText.toString().equals("")) {
					itemText.append("<br>");
				}
				if (itemRequirement.showQuantity())
				{
					itemText.append(itemRequirement.getQuantity()).append(" x ");
				}
				itemText.append(itemRequirement.getName());
			}

			itemLabel.setText("<html><body style = 'text-align:left'>" + itemText + "</body></html>");

			questItemRequirementsListPanel.add(itemLabel);

			questItemRequirementsPanel.add(questItemRequirementsHeader, BorderLayout.NORTH);
			questItemRequirementsPanel.add(questItemRequirementsListPanel, BorderLayout.CENTER);

			bodyPanel.add(questItemRequirementsPanel, BorderLayout.NORTH);
		}

		JPanel questStepsPanel = new JPanel();
		questStepsPanel.setLayout(new BoxLayout(questStepsPanel, BoxLayout.Y_AXIS));
		questStepsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

		for(QuestStep step : panelDetails.getSteps())
		{
			if (!step.isShowInSidebar())
			{
				steps.put(step, null);
				continue;
			}

			JLabel questStepLabel = new JLabel();
			questStepLabel.setLayout(new BorderLayout());
			questStepLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
			questStepLabel.setHorizontalAlignment(SwingConstants.LEFT);
			questStepLabel.setVerticalAlignment(SwingConstants.TOP);

			StringBuilder text = new StringBuilder();

			if(step.equals(currentStep)) {
				questStepLabel.setForeground(ColorScheme.BRAND_ORANGE);
			}
			text.append(step.getText());
			questStepLabel.setText("<html><body style = 'text-align:left'>" + text + "</body></html>");

			steps.put(step, questStepLabel);
			questStepsPanel.add(questStepLabel);
		}

		bodyPanel.add(questStepsPanel, BorderLayout.CENTER);

		add(headerPanel, BorderLayout.NORTH);
		add(bodyPanel, BorderLayout.CENTER);

		if (!panelDetails.getSteps().contains(currentStep))
		{
			collapse();
		}
    }

    public ArrayList<QuestStep> getSteps() {
    	return new ArrayList<>(steps.keySet());
	}

	public void setLockable()
	{
		lockStep.setVisible(true);
	}

    public void updateHighlight(QuestStep currentStep)
	{
		if (currentlyHighlighted != null)
		{
			steps.get(currentlyHighlighted).setForeground(Color.WHITE);
		}
		else
		{
			headerLabel.setForeground(Color.BLACK);
			headerPanel.setBackground(ColorScheme.BRAND_ORANGE);
			viewControls.setBackground(ColorScheme.BRAND_ORANGE);
			leftTitleContainer.setBackground(ColorScheme.BRAND_ORANGE);
		}

		if (steps.get(currentStep) != null)
		{
			steps.get(currentStep).setForeground(ColorScheme.BRAND_ORANGE);
		}
		currentlyHighlighted = currentStep;
	}

	public void removeHighlight()
	{
		if (currentlyHighlighted != null)
		{
			headerLabel.setForeground(Color.WHITE);
			if (isCollapsed())
			{
				applyDimmer(false, headerPanel);
			}
			headerPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
			viewControls.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
			leftTitleContainer.setBackground(ColorScheme.DARKER_GRAY_COLOR.darker());
			if (steps.get(currentlyHighlighted) != null)
			{
				steps.get(currentlyHighlighted).setForeground(Color.WHITE);
			}
			currentlyHighlighted = null;
		}
	}

	public void updateLock()
	{
		if (panelDetails.lockingQuest == null)
		{
			return;
		}

		if (panelDetails.lockingQuest.isUnlockable())
		{
			lockStep.setEnabled(true);
		}
		else
		{
			lockStep.setEnabled(false);
		}

		if (panelDetails.lockingQuest.isLocked())
		{
			lockStep.setSelected(true);
		}
	}

	private void lockSection(boolean locked)
	{
		if (locked)
		{
			panelDetails.lockingQuest.setLockedManually(true);
			if (!isCollapsed())
			{
				collapse();
			}
		}
		else
		{
			panelDetails.lockingQuest.setLockedManually(false);
			if (isCollapsed())
			{
				expand();
			}
		}

	}

	void collapse()
	{
		if (!isCollapsed())
		{
			bodyPanel.setVisible(false);
			applyDimmer(false, headerPanel);
		}
	}

	void expand()
	{
		if (isCollapsed())
		{
			bodyPanel.setVisible(true);
			applyDimmer(true, headerPanel);
		}
	}

	boolean isCollapsed()
	{
		return !bodyPanel.isVisible();
	}

	private void applyDimmer(boolean brighten, JPanel panel)
	{
		for (Component component : panel.getComponents())
		{
			Color color = component.getForeground();

			component.setForeground(brighten ? color.brighter() : color.darker());
		}
	}
}