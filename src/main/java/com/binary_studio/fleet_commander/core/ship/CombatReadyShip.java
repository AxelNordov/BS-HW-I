package com.binary_studio.fleet_commander.core.ship;

import java.util.Optional;

import com.binary_studio.fleet_commander.core.actions.attack.AttackAction;
import com.binary_studio.fleet_commander.core.actions.defence.AttackResult;
import com.binary_studio.fleet_commander.core.actions.defence.RegenerateAction;
import com.binary_studio.fleet_commander.core.common.Attackable;
import com.binary_studio.fleet_commander.core.common.PositiveInteger;
import com.binary_studio.fleet_commander.core.ship.contract.CombatReadyVessel;
import com.binary_studio.fleet_commander.core.subsystems.contract.AttackSubsystem;
import com.binary_studio.fleet_commander.core.subsystems.contract.DefenciveSubsystem;

public final class CombatReadyShip implements CombatReadyVessel {

	private final String name;

	private final PositiveInteger initShieldHP;

	private final PositiveInteger initHullHP;

	private final PositiveInteger powergridOutput;

	private final PositiveInteger initCapacitorAmount;

	private final PositiveInteger capacitorRechargeRate;

	private final PositiveInteger speed;

	private final PositiveInteger size;

	private final AttackSubsystem attackSubsystem;

	private final DefenciveSubsystem defenciveSubsystem;

	private PositiveInteger actualShieldHP;

	private PositiveInteger actualHullHP;

	private PositiveInteger actualCapacitorAmount;

	public CombatReadyShip(String name, PositiveInteger shieldHP, PositiveInteger hullHP,
			PositiveInteger powergridOutput, PositiveInteger capacitorAmount, PositiveInteger capacitorRechargeRate,
			PositiveInteger speed, PositiveInteger size, AttackSubsystem attackSubsystem,
			DefenciveSubsystem defenciveSubsystem) {
		this.name = name;
		this.initShieldHP = shieldHP;
		this.initHullHP = hullHP;
		this.powergridOutput = powergridOutput;
		this.initCapacitorAmount = capacitorAmount;
		this.capacitorRechargeRate = capacitorRechargeRate;
		this.speed = speed;
		this.size = size;
		this.attackSubsystem = attackSubsystem;
		this.defenciveSubsystem = defenciveSubsystem;
		this.actualShieldHP = shieldHP;
		this.actualHullHP = hullHP;
		this.actualCapacitorAmount = capacitorAmount;
	}

	@Override
	public void endTurn() {
		this.actualCapacitorAmount = PositiveInteger.of(Math.min(this.initCapacitorAmount.value(),
				this.actualCapacitorAmount.value() + this.capacitorRechargeRate.value()));
	}

	@Override
	public void startTurn() {
		// this method is empty
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public PositiveInteger getSize() {
		return this.size;
	}

	@Override
	public PositiveInteger getCurrentSpeed() {
		return this.speed;
	}

	@Override
	public Optional<AttackAction> attack(Attackable target) {
		if (this.actualCapacitorAmount.value() < this.attackSubsystem.getCapacitorConsumption().value()) {
			return Optional.empty();
		}

		this.actualCapacitorAmount = PositiveInteger
				.of(this.actualCapacitorAmount.value() - this.attackSubsystem.getCapacitorConsumption().value());

		AttackAction attackAction = new AttackAction(this.attackSubsystem.attack(target), this, target,
				this.attackSubsystem);
		return Optional.of(attackAction);
	}

	@Override
	public AttackResult applyAttack(AttackAction attack) {
		PositiveInteger resultDamage = this.defenciveSubsystem.reduceDamage(attack).damage;
		if (resultDamage.value() > this.actualShieldHP.value() + this.actualHullHP.value()) {
			return new AttackResult.Destroyed();
		}

		if (this.actualShieldHP.value() >= resultDamage.value()) {
			this.actualShieldHP = PositiveInteger.of(this.actualShieldHP.value() - resultDamage.value());
		}
		else {
			this.actualHullHP = PositiveInteger
					.of(this.actualHullHP.value() - (resultDamage.value() - this.actualShieldHP.value()));
			this.actualShieldHP = PositiveInteger.of(0);
		}
		return new AttackResult.DamageRecived(attack.weapon, resultDamage, attack.target);
	}

	@Override
	public Optional<RegenerateAction> regenerate() {
		if (this.actualCapacitorAmount.value() < this.defenciveSubsystem.getCapacitorConsumption().value()) {
			return Optional.empty();
		}

		this.actualCapacitorAmount = PositiveInteger
				.of(this.actualCapacitorAmount.value() - this.defenciveSubsystem.getCapacitorConsumption().value());
		PositiveInteger resultShieldRegenerated = PositiveInteger.of(0);
		PositiveInteger currentShieldRegenerated = this.defenciveSubsystem.regenerate().shieldHPRegenerated;
		if (this.actualShieldHP.value() < this.initShieldHP.value()) {
			if (this.initShieldHP.value() - this.actualShieldHP.value() < currentShieldRegenerated.value()) {
				resultShieldRegenerated = PositiveInteger.of(this.initShieldHP.value() - this.actualShieldHP.value());
			}
			else {
				resultShieldRegenerated = currentShieldRegenerated;
			}
		}

		PositiveInteger resultHullRegenerated = PositiveInteger.of(0);
		PositiveInteger currentHullRegenerated = this.defenciveSubsystem.regenerate().hullHPRegenerated;
		if (this.actualHullHP.value() < this.initHullHP.value()) {
			if (this.initHullHP.value() - this.actualHullHP.value() < currentHullRegenerated.value()) {
				resultHullRegenerated = PositiveInteger.of(this.initHullHP.value() - this.actualHullHP.value());
			}
			else {
				resultHullRegenerated = currentHullRegenerated;
			}
		}

		RegenerateAction regenerateAction = new RegenerateAction(resultShieldRegenerated, resultHullRegenerated);
		return Optional.of(regenerateAction);
	}

}
