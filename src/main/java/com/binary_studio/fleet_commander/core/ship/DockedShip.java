package com.binary_studio.fleet_commander.core.ship;

import java.util.Optional;

import com.binary_studio.fleet_commander.core.common.PositiveInteger;
import com.binary_studio.fleet_commander.core.exceptions.InsufficientPowergridException;
import com.binary_studio.fleet_commander.core.exceptions.NotAllSubsystemsFitted;
import com.binary_studio.fleet_commander.core.ship.contract.ModularVessel;
import com.binary_studio.fleet_commander.core.subsystems.contract.AttackSubsystem;
import com.binary_studio.fleet_commander.core.subsystems.contract.DefenciveSubsystem;
import com.binary_studio.fleet_commander.core.subsystems.contract.Subsystem;

public final class DockedShip implements ModularVessel {

	private final String name;

	private final PositiveInteger shieldHP;

	private final PositiveInteger hullHP;

	private final PositiveInteger powergridOutput;

	private final PositiveInteger capacitorAmount;

	private final PositiveInteger capacitorRechargeRate;

	private final PositiveInteger speed;

	private final PositiveInteger size;

	private Optional<AttackSubsystem> attackSubsystem = Optional.empty();

	private Optional<DefenciveSubsystem> defenciveSubsystem = Optional.empty();

	private DockedShip(String name, PositiveInteger shieldHP, PositiveInteger hullHP, PositiveInteger powergridOutput,
			PositiveInteger capacitorAmount, PositiveInteger capacitorRechargeRate, PositiveInteger speed,
			PositiveInteger size) {
		if (name == null || name.isBlank()) {
			throw new IllegalArgumentException("Name should be not null and not empty");
		}
		this.name = name;
		this.shieldHP = shieldHP;
		this.hullHP = hullHP;
		this.powergridOutput = powergridOutput;
		this.capacitorAmount = capacitorAmount;
		this.capacitorRechargeRate = capacitorRechargeRate;
		this.speed = speed;
		this.size = size;
	}

	public static DockedShip construct(String name, PositiveInteger shieldHP, PositiveInteger hullHP,
			PositiveInteger powergridOutput, PositiveInteger capacitorAmount, PositiveInteger capacitorRechargeRate,
			PositiveInteger speed, PositiveInteger size) {
		return new DockedShip(name, shieldHP, hullHP, powergridOutput, capacitorAmount, capacitorRechargeRate, speed,
				size);
	}

	@Override
	public void fitAttackSubsystem(AttackSubsystem subsystem) throws InsufficientPowergridException {
		if (subsystem == null) {
			this.attackSubsystem = Optional.empty();
			return;
		}
		int availablePowergridOutput = this.powergridOutput.value()
				- this.defenciveSubsystem.map(Subsystem::getPowerGridConsumption).map(PositiveInteger::value).orElse(0);
		int afterInstallPowerGridConsumption = availablePowergridOutput - subsystem.getPowerGridConsumption().value();
		if (afterInstallPowerGridConsumption < 0) {
			throw new InsufficientPowergridException(afterInstallPowerGridConsumption * -1);
		}
		this.attackSubsystem = Optional.of(subsystem);
	}

	@Override
	public void fitDefensiveSubsystem(DefenciveSubsystem subsystem) throws InsufficientPowergridException {
		if (subsystem == null) {
			this.defenciveSubsystem = Optional.empty();
			return;
		}
		int availablePowergridOutput = this.powergridOutput.value()
				- this.attackSubsystem.map(Subsystem::getPowerGridConsumption).map(PositiveInteger::value).orElse(0);
		int afterInstallPowerGridConsumption = availablePowergridOutput - subsystem.getPowerGridConsumption().value();
		if (afterInstallPowerGridConsumption < 0) {
			throw new InsufficientPowergridException(afterInstallPowerGridConsumption * -1);
		}
		this.defenciveSubsystem = Optional.of(subsystem);
	}

	public CombatReadyShip undock() throws NotAllSubsystemsFitted {
		if (this.attackSubsystem.isEmpty() && this.defenciveSubsystem.isEmpty()) {
			throw NotAllSubsystemsFitted.bothMissing();
		}
		if (this.attackSubsystem.isEmpty()) {
			throw NotAllSubsystemsFitted.attackMissing();
		}
		if (this.defenciveSubsystem.isEmpty()) {
			throw NotAllSubsystemsFitted.defenciveMissing();
		}
		return new CombatReadyShip(this.name, this.shieldHP, this.hullHP, this.powergridOutput, this.capacitorAmount,
				this.capacitorRechargeRate, this.speed, this.size, this.attackSubsystem.get(),
				this.defenciveSubsystem.get());
	}

}
