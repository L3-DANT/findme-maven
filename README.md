#findme

iOS app allowing people to find their friends on a map

###Warning

If you are using Glassfish 4.1.1 there's a bug causing the Classloader being unable to find Jersey's Json parser.
<br>To get rid of it, follow these steps :

* Download [MANIFEST.MF](https://bugs.eclipse.org/bugs/attachment.cgi?id=251917)
* Replace the MANIFEST.MF file in the org.eclipse.persistence.moxy.jar file with the one you downloaded.
<br>( path\to\glassfish4\glassfish\modules\org.eclipse.persistence.moxy.jar)
* Restart Glassfish