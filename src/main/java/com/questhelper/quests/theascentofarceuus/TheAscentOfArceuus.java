/*
 * Copyright (c) 2021, Zoinkwiz
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
package com.questhelper.quests.theascentofarceuus;

import com.questhelper.ItemCollections;
import com.questhelper.QuestDescriptor;
import com.questhelper.QuestHelperQuest;
import com.questhelper.Zone;
import com.questhelper.banktab.BankSlotIcons;
import com.questhelper.panel.PanelDetails;
import com.questhelper.questhelpers.BasicQuestHelper;
import com.questhelper.requirements.FavourRequirement;
import com.questhelper.requirements.ItemRequirement;
import com.questhelper.requirements.QuestRequirement;
import com.questhelper.requirements.Requirement;
import com.questhelper.requirements.SkillRequirement;
import com.questhelper.steps.ConditionalStep;
import com.questhelper.steps.NpcStep;
import com.questhelper.steps.ObjectStep;
import com.questhelper.steps.QuestStep;
import com.questhelper.steps.conditional.ConditionForStep;
import com.questhelper.steps.conditional.Conditions;
import com.questhelper.steps.conditional.InInstanceCondition;
import com.questhelper.steps.conditional.NpcHintArrowCondition;
import com.questhelper.steps.conditional.VarbitCondition;
import com.questhelper.steps.conditional.ZoneCondition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Favour;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.NullObjectID;
import net.runelite.api.ObjectID;
import net.runelite.api.QuestState;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;

@QuestDescriptor(
	quest = QuestHelperQuest.THE_ASCENT_OF_ARCEUUS
)
public class TheAscentOfArceuus extends BasicQuestHelper
{
	// Recommended
	ItemRequirement combatGear, dramenStaff, battlefrontTeleports2, xericsTalisman, skillsNecklace;

	ConditionForStep inTowerInstance, inTowerF1, inKaruulm, inCastle, foundTrack1, foundTrack2, foundTrack3,
		foundTrack4, foundTrack5, trappedSoulNearby;

	QuestStep talkToMori, goUpToAndrews, talkToAndrews, returnToMori, enterTowerOfMagic, killTormentedSouls,
		goUpstairsTowerOfMagic, talkToArceuus, enterKaruulm, talkToKaal, leaveKaal, inspectGrave, inspectTrack1,
		inspectTrack2, inspectTrack3, inspectTrack4, inspectTrack5, inspectTrack6, killTrappedSoul, enterKaruulmAgain,
		talkToKaalAgain, searchRocks, goUpstairsInTowerToFinish, talkToArceuusToFinish;

	Zone towerF0, towerF1, karuulm, castle;

	@Override
	public Map<Integer, QuestStep> loadSteps()
	{
		loadZones();
		setupRequirements();
		setupConditions();
		setupSteps();
		Map<Integer, QuestStep> steps = new HashMap<>();

		steps.put(0, talkToMori);

		ConditionalStep goTalkToAndrews = new ConditionalStep(this, goUpToAndrews);
		goTalkToAndrews.addStep(inCastle, talkToAndrews);
		steps.put(1, goTalkToAndrews);

		steps.put(2, returnToMori);

		ConditionalStep goDefeatSouls = new ConditionalStep(this, enterTowerOfMagic);
		goDefeatSouls.addStep(inTowerInstance, killTormentedSouls);
		steps.put(3, goDefeatSouls);
		steps.put(4, goDefeatSouls);

		ConditionalStep goTalkToArceuus = new ConditionalStep(this, goUpstairsTowerOfMagic);
		goTalkToArceuus.addStep(inTowerF1, talkToArceuus);
		steps.put(5, goTalkToArceuus);
		steps.put(6, goTalkToArceuus);

		ConditionalStep goTalkToKaal = new ConditionalStep(this, enterKaruulm);
		goTalkToKaal.addStep(inKaruulm, talkToKaal);
		steps.put(7, goTalkToKaal);

		ConditionalStep goInspectGrave = new ConditionalStep(this, inspectGrave);
		goInspectGrave.addStep(inKaruulm, leaveKaal);
		steps.put(8, goInspectGrave);

		ConditionalStep trackingSteps = new ConditionalStep(this, inspectTrack1);
		trackingSteps.addStep(foundTrack5, inspectTrack6);
		trackingSteps.addStep(foundTrack4, inspectTrack5);
		trackingSteps.addStep(foundTrack3, inspectTrack4);
		trackingSteps.addStep(foundTrack2, inspectTrack3);
		trackingSteps.addStep(foundTrack1, inspectTrack2);
		steps.put(9, trackingSteps);

		ConditionalStep goKillTrappedSoul = new ConditionalStep(this, inspectTrack6);
		goKillTrappedSoul.addStep(trappedSoulNearby, killTrappedSoul);
		steps.put(10, goKillTrappedSoul);

		ConditionalStep goReturnToKaal = new ConditionalStep(this, enterKaruulmAgain);
		goReturnToKaal.addStep(inKaruulm, talkToKaalAgain);
		steps.put(11, goReturnToKaal);

		steps.put(12, searchRocks);

		ConditionalStep goFinishQuest = new ConditionalStep(this, goUpstairsInTowerToFinish);
		goFinishQuest.addStep(inTowerF1, talkToArceuusToFinish);
		steps.put(13, goFinishQuest);

		return steps;
	}

	public void setupRequirements()
	{
		dramenStaff = new ItemRequirement("Access to Fairy Rings", ItemID.DRAMEN_STAFF);
		dramenStaff.addAlternates(ItemID.LUNAR_STAFF);
		battlefrontTeleports2 = new ItemRequirement("Battlefront teleport", ItemID.BATTLEFRONT_TELEPORT, 2);
		xericsTalisman = new ItemRequirement("Xeric's Talisman", ItemID.XERICS_TALISMAN);
		skillsNecklace = new ItemRequirement("Skill's necklace", ItemCollections.getSkillsNecklaces());

		combatGear = new ItemRequirement("Combat gear", -1, -1);
		combatGear.setDisplayItemId(BankSlotIcons.getCombatGear());
	}

	public void loadZones()
	{
		towerF0 = new Zone(new WorldPoint(1563, 3802, 0), new WorldPoint(1595, 3836, 0));
		towerF1 = new Zone(new WorldPoint(1563, 3802, 1), new WorldPoint(1595, 3836, 1));
		castle = new Zone(new WorldPoint(1591, 3654, 1), new WorldPoint(1628, 3692, 1));
		karuulm = new Zone(new WorldPoint(1249, 10144, 0), new WorldPoint(1385, 10286, 0));
	}

	public void setupConditions()
	{
		inTowerInstance = new Conditions(new InInstanceCondition(), new ZoneCondition(towerF0));
		inTowerF1 = new ZoneCondition(towerF1);
		inCastle = new ZoneCondition(castle);
		inKaruulm = new ZoneCondition(karuulm);

		foundTrack1 = new VarbitCondition(7860, 1);
		foundTrack2 = new VarbitCondition(7861, 1);
		foundTrack3 = new VarbitCondition(7862, 1);
		foundTrack4 = new VarbitCondition(7863, 1);
		foundTrack5 = new VarbitCondition(7864, 1);

		trappedSoulNearby = new NpcHintArrowCondition(NpcID.TRAPPED_SOUL);
		// Inspected grave:
		// 7859 0->1
		// 7856 8->9
	}

	public void setupSteps()
	{
		talkToMori = new NpcStep(this, NpcID.MORI, new WorldPoint(1698, 3742, 0), "Talk to Mori in Arceuus.");
		talkToMori.addDialogSteps("What can I do to help?", "We should let someone know about this.",
			"Of course I'll help.");

		goUpToAndrews = new ObjectStep(this, ObjectID.STAIRCASE_11807, new WorldPoint(1616, 3681, 0),
			"Talk to Councillor Andrews in Kourend Castle.");
		talkToAndrews = new NpcStep(this, NpcID.COUNCILLOR_ANDREWS, new WorldPoint(1620, 3673, 1),
			"Talk to Councillor Andrews in Kourend Castle.");
		talkToAndrews.addDialogStep("There's been a death in Arceuus.");
		talkToAndrews.addSubSteps(goUpToAndrews);

		returnToMori = new NpcStep(this, NpcID.MORI, new WorldPoint(1698, 3742, 0), "Return to Mori in Arceuus.");
		returnToMori.addDialogStep("What should we do now?");

		enterTowerOfMagic = new ObjectStep(this, ObjectID.DOOR_33570, new WorldPoint(1596, 3820, 0), "Enter the Tower" +
			" of Magic in Arceuus, ready to fight some level 16 Tormented Souls.", combatGear);
		enterTowerOfMagic.addDialogStep("Yes.");

		killTormentedSouls = new NpcStep(this, NpcID.TORMENTED_SOUL, new WorldPoint(1585, 3821, 0),
			"Defeat the tormented souls.", true, combatGear);
		((NpcStep) killTormentedSouls).addAlternateNpcs(NpcID.TORMENTED_SOUL_8513);

		goUpstairsTowerOfMagic = new ObjectStep(this, ObjectID.STAIRS_33575, new WorldPoint(1585, 3821, 0),
			"Go up the stairs in the Tower of Magic.");

		talkToArceuus = new NpcStep(this, NpcID.LORD_TROBIN_ARCEUUS, new WorldPoint(1580, 3821, 1),
			"Talk to Lord Trobin Arceuus.");
		((NpcStep) talkToArceuus).addAlternateNpcs(NpcID.LORD_TROBIN_ARCEUUS_8505);

		enterKaruulm = new ObjectStep(this, ObjectID.ELEVATOR, new WorldPoint(1311, 3807, 0),
			"Go down the elevator on Mount Karuulm.");

		talkToKaal = new NpcStep(this, NpcID.KAALKETJOR, new WorldPoint(1312, 10211, 0), "Talk to Kaal-Ket-Jor.");


		leaveKaal = new ObjectStep(this, ObjectID.CAVE_EXIT_34514, new WorldPoint(1312, 10186, 0),
			"Inspect the ancient grave south of Mount Karuulm.");

		inspectGrave = new ObjectStep(this, NullObjectID.NULL_34602, new WorldPoint(1349, 3737, 0),
			"Inspect the ancient grave south of Mount Karuulm.", combatGear);
		inspectGrave.addSubSteps(leaveKaal);

		inspectTrack1 = new ObjectStep(this, NullObjectID.NULL_34622, new WorldPoint(1335, 3743, 0),
			"Inspect plants and bushes until you uncover the full path.");
		inspectTrack2 = new ObjectStep(this, NullObjectID.NULL_34623, new WorldPoint(1317, 3750, 0),
			"Inspect plants and bushes until you uncover the full path.");
		inspectTrack3 = new ObjectStep(this, NullObjectID.NULL_34623, new WorldPoint(1305, 3750, 0),
			"Inspect plants and bushes until you uncover the full path.");
		inspectTrack4 = new ObjectStep(this, NullObjectID.NULL_34621, new WorldPoint(1288, 3751, 0),
			"Inspect plants and bushes until you uncover the full path.");
		inspectTrack5 = new ObjectStep(this, NullObjectID.NULL_34624, new WorldPoint(1286, 3738, 0),
			"Inspect plants and bushes until you uncover the full path.");
		inspectTrack1.addSubSteps(inspectTrack2, inspectTrack3, inspectTrack4, inspectTrack5);

		inspectTrack6 = new ObjectStep(this, NullObjectID.NULL_34625, new WorldPoint(1282, 3726, 0),
			"Inspect the final plant and kill the Tormented Soul (level 30) which appears.", combatGear);
		killTrappedSoul = new NpcStep(this, NpcID.TRAPPED_SOUL, new WorldPoint(1281, 3724, 0),
			"Kill the Trapped Soul.");
		inspectTrack6.addSubSteps(killTrappedSoul);

		enterKaruulmAgain = new ObjectStep(this, ObjectID.ELEVATOR, new WorldPoint(1311, 3807, 0),
			"Return to Kaal-Ket-Jor.");
		talkToKaalAgain = new NpcStep(this, NpcID.KAALKETJOR, new WorldPoint(1312, 10211, 0),
			"Return to Kaal-Ket-Jor.");
		talkToKaalAgain.addSubSteps(enterKaruulmAgain);

		searchRocks = new ObjectStep(this, NullObjectID.NULL_34627, new WorldPoint(1714, 3876, 0),
			"Inspect some rocks near the Arceuus Altar.");

		goUpstairsInTowerToFinish = new ObjectStep(this, ObjectID.STAIRS_33575, new WorldPoint(1585, 3821, 0),
			"Return to Lord Trobin Arceuus to finish the quest.");
		talkToArceuusToFinish = new NpcStep(this, NpcID.LORD_TROBIN_ARCEUUS_8505, new WorldPoint(1580, 3821, 1),
			"Talk to Lord Trobin Arceuus to finish the quest.");
		talkToArceuusToFinish.addSubSteps(goUpstairsInTowerToFinish);
	}

	@Override
	public ArrayList<ItemRequirement> getItemRecommended()
	{
		return new ArrayList<>(Arrays.asList(combatGear, dramenStaff, battlefrontTeleports2, xericsTalisman, skillsNecklace));
	}

	@Override
	public ArrayList<String> getCombatRequirements()
	{
		return new ArrayList<>(Arrays.asList("5 Tormented Souls (level 16)", "Trapped Soul (level 30)"));
	}

	@Override
	public ArrayList<Requirement> getGeneralRequirements()
	{
		ArrayList<Requirement> req = new ArrayList<>();
		req.add(new QuestRequirement(QuestHelperQuest.CLIENT_OF_KOUREND, QuestState.FINISHED));
		req.add(new FavourRequirement(Favour.ARCEUUS, 20));
		req.add(new SkillRequirement(Skill.HUNTER, 12));
		return req;
	}

	@Override
	public ArrayList<PanelDetails> getPanels()
	{
		ArrayList<PanelDetails> allSteps = new ArrayList<>();
		allSteps.add(new PanelDetails("Starting off",
			new ArrayList<>(Arrays.asList(talkToMori, talkToAndrews, returnToMori, enterTowerOfMagic,
				killTormentedSouls, goUpstairsTowerOfMagic, talkToArceuus))));
		allSteps.add(new PanelDetails("Freeing a Soul",
			new ArrayList<>(Arrays.asList(talkToKaal, inspectGrave, inspectTrack1, inspectTrack6, talkToKaalAgain)),
			combatGear));
		allSteps.add(new PanelDetails("Saving Arceuus",
			new ArrayList<>(Arrays.asList(searchRocks, talkToArceuusToFinish))));
		return allSteps;
	}
}