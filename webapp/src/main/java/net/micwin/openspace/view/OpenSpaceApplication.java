package net.micwin.openspace.view;

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

import java.util.Date;

import net.micwin.openspace.view.welcome.WelcomePage;

import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenSpaceApplication extends WebApplication {

	private static final Logger L = LoggerFactory.getLogger(OpenSpaceApplication.class);
	
	private Date startupTime = new Date();
	
	public OpenSpaceApplication() {
	}

	@Override
	public void init() {
		super.init() ; 
		getComponentInstantiationListeners().add(new SpringComponentInjector(this)) ;
		
		
		L.info("OpenSpaceApplication initialized");

	}

	@Override
	public Class<? extends Page> getHomePage() {
		return WelcomePage.class;
	}

	@Override
	public Session newSession(Request request, Response response) {

		return new OpenSpaceSession(request);
	}

	@Override
	protected void onDestroy() {

	}

	public Date getStartupTime() {
		return startupTime;
	}

}
