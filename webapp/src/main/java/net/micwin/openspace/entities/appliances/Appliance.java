package net.micwin.openspace.entities.appliances;

/*
 (c) 2012 micwin.net

 This file is part of open-space.

 open-space is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 open-space is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero Public License for more details.

 You should have received a copy of the GNU Affero Public License
 along with open-space.  If not, see http://www.gnu.org/licenses.

 Diese Datei ist Teil von open-space.

 open-space ist Freie Software: Sie können es unter den Bedingungen
 der GNU Affero Public License, wie von der Free Software Foundation,
 Version 3 der Lizenz oder (nach Ihrer Option) jeder späteren
 veröffentlichten Version, weiterverbreiten und/oder modifizieren.

 open-space wird in der Hoffnung, dass es nützlich sein wird, aber
 OHNE JEDE GEWÄHRLEISTUNG, bereitgestellt; sogar ohne die implizite
 Gewährleistung der MARKTFÄHIGKEIT oder EIGNUNG FÜR EINEN BESTIMMTEN ZWECK.
 Siehe die GNU Affero Public License für weitere Details.



 Sie sollten eine Kopie der GNU Affero Public License zusammen mit diesem
 Programm erhalten haben. Wenn nicht, siehe http://www.gnu.org/licenses. 

 */
/**
 * An application is a certain use or action or ability an Avatar / NPC or an
 * object can do.
 * 
 * @author MicWin
 * 
 */
public enum Appliance {

	/**
	 * 
	 * Designing buildings. Architecture defines how complex buildings may be,
	 * that is, how many modules (Utilizations of appliances) may be part of a
	 * building.
	 */
	ARCHITECTURE("Architektur", "Sollte man beherrschen wenn man Gebäude bauen will"),

	/**
	 * Building living rooms for Avatars, NPCs and PCs.
	 */
	HABITATS("Habitate",
					"Wohnviertel in denen biologische Lebensformen eine kontrollierte Umwelt und Lebenserhaltung vorfinden."),

	NANITE_MANAGEMENT(
					"Naniten-Management",
					"Unterprogramm um  Naniten zu kontrollieren. Pro Stufe verdoppelt sich die Gesamtmenge der beherrschbaren Naniten; die Anzahl der maximalen Gruppen erhöht sich um 1 je Stufe. Steigt durch Verwendung der Funktionen 'Teilen', 'Verdoppeln', 'Gate-Reise' und 'Gruppe löschen''"),

	NANITE_BATTLE(
					"Naniten-Angriff",
					"Unterprogramm um Naniten anzugreifen. Pro Stufe wächst der Schaden jedes angreifenden Nanits um 10%. Steigt durch Verwendung der Funktion 'Angreifen'"),

	NANITE_DAMAGE_CONTROL(
					"Naniten-Schadenskontrolle",
					"Unterprogramm um erlittenen Schaden abzuwehren und umzuleiten. Pro Stufe mindert sich der erlittene Schaden um 10%. Steigt sobald eine Gruppe angegriffen wird."),

	NANITE_CRITICAL_HIT(
					"Naniten-Piercing",
					"Upgrade des Programms 'Naniten-Angriff' um beim Nanitenkampf kritischen Schaden anzubringen. Jede Stufe erhöht bei jedem Kampf die Chance, den dreifachen Schaden zu verursachen, um 5%. Steigt wenn eine gegnerische Gruppe vollständig zerstört wird."),

	SHORT_RANGE_SCANS(
					"Kurzstrecken-Scanner (SRS)",
					"Der SRS scannt beim Eintritt in eine Umgebung diese voll-automatisch. Je höher die Stufe, desto kleinere (und besser abgeschirmte) Objekte können gescannt werden. Steigt mit jedem Scanvorgang."),

	EMISSION_CONTROL(
					"EmissionsKontrollProgramm (ECP)",
					"Optimiert die Energie-Emissionen der Nanitengruppen um für andere so schwer auffindbar wie nur möglich zu sein. Pro Stufe wird die Signaturstärke um 5% gesenkt. Wird aktiviert, sobald  eine fremde Einheit nicht gescannt werden konnte und steigt mit jedem Scan, der Einheiten nicht entdecken konnte.");

	private String label;

	private String description;

	/**
	 * The complexity that one unit / component of level 1 of this appliance
	 * has.
	 * 
	 * @return
	 */

	private Appliance(String label, String description) {
		this.label = label;
		this.description = description;

	}

	public String getLabel() {
		return label;
	}

	public String getDescription() {
		return description;
	}

}
