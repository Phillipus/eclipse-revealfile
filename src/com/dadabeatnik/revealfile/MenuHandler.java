/*******************************************************************************
 * Copyright (c) 2014 Phillip Beauvoir
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Phillip Beauvoir
 *******************************************************************************/
package com.dadabeatnik.revealfile;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;


public class MenuHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IStructuredSelection selection = (IStructuredSelection)HandlerUtil.getActiveMenuSelection(event);
        Object firstElement = selection.getFirstElement();
        
        File file = null;
        
        if(firstElement instanceof IJavaElement) {
            try {
                IResource resource = ((IJavaElement)firstElement).getCorrespondingResource();
                IPath path = resource.getLocation();
                if(path != null) {
                    file = path.toFile();
                }
            }
            catch(JavaModelException ex) {
                ex.printStackTrace();
            }
        }
        else if(firstElement instanceof IResource) {
            IPath path = ((IResource)firstElement).getLocation();
            if(path != null) {
                file = path.toFile();
            }
        }
        
        if(file != null) {
            open(file);
        }
       
        return null;
    }
    
    private void open(File file) {
        String s = file.getAbsolutePath();
        s = "file:///" + s.replaceAll(" ", "%20");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

        try {
            if(isWindows()) {
                new ProcessBuilder("explorer.exe", "/select," + s).start(); //$NON-NLS-1$ //$NON-NLS-2$
            }
            else if(isMac()) {
                Runtime.getRuntime().exec("open -R " + s); //$NON-NLS-1$
            }
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static boolean isWindows() {
        return Platform.getOS().equals(Platform.OS_WIN32);
    }
    
    public static boolean isMac() {
        return Platform.getOS().equals(Platform.OS_MACOSX);
    }
}
