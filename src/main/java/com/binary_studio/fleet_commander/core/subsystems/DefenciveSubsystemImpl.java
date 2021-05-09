package com.binary_studio.fleet_commander.core.subsystems;

import com.binary_studio.fleet_commander.core.actions.attack.AttackAction;
import com.binary_studio.fleet_commander.core.actions.defence.RegenerateAction;
import com.binary_studio.fleet_commander.core.common.PositiveInteger;
import com.binary_studio.fleet_commander.core.subsystems.contract.DefenciveSubsystem;

public final class DefenciveSubsystemImpl implements DefenciveSubsystem {

	private String name;

	private PositiveInteger powergridConsumption;

	private PositiveInteger capacitorConsumption;

	private PositiveInteger impactReductionPercent;

	private PositiveInteger shieldRegeneration;

	private PositiveInteger hullRegeneration;

	private DefenciveSubsystemImpl(String name, PositiveInteger powergridConsumption,
			PositiveInteger capacitorConsumption, PositiveInteger impactReductionPercent,
			PositiveInteger shieldRegeneration, PositiveInteger hullRegeneration) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Name should be not null and not empty");
		}
		this.name = name;
		this.powergridConsumption = powergridConsumption;
		this.capacitorConsumption = capacitorConsumption;
		this.impactReductionPercent = impactReductionPercent;
		this.shieldRegeneration = shieldRegeneration;
		this.hullRegeneration = hullRegeneration;
	}

	public static DefenciveSubsystemImpl construct(String name, PositiveInteger powergridConsumption,
			PositiveInteger capacitorConsumption, PositiveInteger impactReductionPercent,
			PositiveInteger shieldRegeneration, PositiveInteger hullRegeneration) throws IllegalArgumentException {
		return new DefenciveSubsystemImpl(name, powergridConsumption, capacitorConsumption, impactReductionPercent,
				shieldRegeneration, hullRegeneration);
	}

	@Override
	public PositiveInteger getPowerGridConsumption() {
		return this.powergridConsumption;
	}

	@Override
	public PositiveInteger getCapacitorConsumption() {
		return this.capacitorConsumption;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public AttackAction reduceDamage(AttackAction incomingDamage) {
		int correctImpactReductionPercent = this.impactReductionPercent.value() > 95 ? 95
				: this.impactReductionPercent.value();
		PositiveInteger damage = PositiveInteger
				.of((int) Math.ceil(incomingDamage.damage.value() * (100 - correctImpactReductionPercent) / 100.0));
		return new AttackAction(damage, incomingDamage.attacker, incomingDamage.target, incomingDamage.weapon);
	}

	@Override
	public RegenerateAction regenerate() {
		return new RegenerateAction(this.shieldRegeneration, this.hullRegeneration);
	}

}
